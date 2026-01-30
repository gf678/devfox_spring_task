package com.littlestar.task.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

// ログインリクエストをキャッチして認証を行い、成功した場合にJWTを発行するフィルター
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // ログイン試行をキャッチするメソッド
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // クライアントのログインリクエストから username と password を取得
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // セキュリティで使用するための一時的な認証トークンを作成
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        // AuthenticationManager に検証を委譲
        return authenticationManager.authenticate(authRequest);
    }

    // 認証が成功した場合に実行されるメソッド (ここでJWTを発行)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {

        // 認証結果オブジェクトからユーザー情報を取得
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = customUserDetails.getUsername();

        // 権限情報を抽出
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // JwtUtilを使用して、1時間有効なJWTトークンを生成
        String token = jwtUtil.createJwt(username, role, 60 * 60 * 1000L);

        // HTTPレスポンスヘッダーに "Authorization: Bearer [token]" 形式で追加
        response.addHeader("Authorization", "Bearer " + token);
    }

    // 認証が失敗した場合に実行されるメソッド
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        // 401 Unauthorized ステータスコードを返す
        response.setStatus(401);
    }
}
