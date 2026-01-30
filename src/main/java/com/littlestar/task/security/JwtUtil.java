package com.littlestar.task.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT トークンの生成、情報抽出、有効期限確認など、ライブラリによる実際の演算を担当するユーティリティクラス
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    // application.yml のシークレットキーをロードし、SHA-256 アルゴリズム用のキーを生成
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // トークンから 'username' クレームを抽出
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    // トークンから 'role' クレームを抽出
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // トークンの有効期限が切れているか確認
    public Boolean isTokenExpired(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            // 署名不一致、トークン改ざん、期限切れなどの例外発生時、安全に '期限切れ' として処理
            return true;
        }
    }

    // 新しい JWT トークンを生成
    public String createJwt(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username) // データを格納
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 発行時間
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 有効期限
                .signWith(secretKey) // 暗号署名
                .compact();
    }
}