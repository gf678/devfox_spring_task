package com.littlestar.task.security;

import com.littlestar.task.entity.Role;
import com.littlestar.task.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// すべてのリクエストごとに実行され、クッキーに含まれるJWTを検証してユーザーを認証するフィルター
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Cookie[] cookies = request.getCookies();

        // クライアントのクッキーから "Authorization" という名前のトークンクッキーを取得
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // トークンが存在しない場合：フィルターチェーンを続行して次のフィルターに渡す
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. トークンの有効期限を検証
            if (jwtUtil.isTokenExpired(token)) {
                log.warn("トークンの有効期限が切れています");
                filterChain.doFilter(request, response);
                return;
            }

            // JWT 内のクレーム(Claim)からユーザー情報を取得
            String username = jwtUtil.getUsername(token);
            Role role = Role.valueOf(jwtUtil.getRole(token));

            // 認証処理用の一時的な User エンティティおよび CustomUserDetails を作成
            User user = User.builder()
                    .loginId(username)
                    .password("N/A") // 認証が完了しているため、パスワードは不要
                    .role(role)
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            // Spring Security 認証トークンを作成
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities());

            // SecurityContext に認証情報を保存
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception e) {
            log.error("JWTフィルター処理中にエラーが発生しました。: {}", e.getMessage(), e);
        }

        // 最後に、次のフィルターにリクエストを渡す
        filterChain.doFilter(request, response);
    }
}
