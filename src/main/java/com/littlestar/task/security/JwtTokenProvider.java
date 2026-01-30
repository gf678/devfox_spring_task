package com.littlestar.task.security;

import com.littlestar.task.domain.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// JWT トークンの生成、発行、有効性検証を担当するクラス
@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    // application.yml の secret 値を基に暗号化キーを生成
    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // ユーザー情報に基づき AccessToken と RefreshToken を生成
    public JwtToken generateToken(Authentication authentication) {
        // 権限リストを文字列に変換 (例: "ROLE_USER,ROLE_ADMIN")
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token の生成
        Date accessTokenExpiresIn = new Date(now + 86400000);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       // トークンのサブジェクト (username)
                .claim("auth", authorities)                // カスタムクレーム (権限)
                .setExpiration(accessTokenExpiresIn)       // 有効期限
                .signWith(key, SignatureAlgorithm.HS256)   // 暗号化アルゴリズムとキー
                .compact();

        // Refresh Token の生成
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT トークンを復号化し、トークンに含まれるユーザー情報を取得
    public Authentication getAuthentication(String accessToken) {
        // トークン復号化
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("権限情報がないトークンです。");
        }

        // クレームから権限情報を取得
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Spring Security 用の UserDetails オブジェクトを生成
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // トークンの有効性を検証
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("無効なJWT署名です。");
        } catch (ExpiredJwtException e) {
            log.info("期限切れのJWTトークンです。");
        } catch (UnsupportedJwtException e) {
            log.info("サポートされていないJWTトークンです。");
        } catch (IllegalArgumentException e) {
            log.info("JWTクレーム文字列が空です。");
        }
        return false;
    }

    // トークン復号化および例外処理
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
