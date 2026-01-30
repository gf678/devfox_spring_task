package com.littlestar.task.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

// クライアントのリクエストヘッダーからJWTを抽出し、検証後に認証情報をSecurityContextに登録するフィルター
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // リクエストヘッダーから 'Authorization' フィールドのJWTトークンを抽出
        String token = resolveToken((HttpServletRequest) request);

        // 2. 抽出したトークンが存在し、jwtTokenProviderによる検証結果が有効であれば処理を行う
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // トークンが有効な場合、トークンから認証(Authentication)オブジェクトを取得
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // 取得した認証オブジェクトをSecurityContextHolderのContextに保存
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 次のフィルターにリクエストとレスポンスを渡す
        chain.doFilter(request, response);
    }

    // リクエストヘッダーからトークン情報を抽出するヘルパーメソッド
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " の後のトークン値のみ返す
        }
        return null;
    }
}
