package com.littlestar.task.configuration;

import com.littlestar.task.security.JwtFilter;
import com.littlestar.task.security.JwtUtil;
import com.littlestar.task.security.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity // Spring Securityの設定を有効化する
@RequiredArgsConstructor
public class SecurityConfiguration {

    // 認証マネージャー生成のための設定Bean
    private final AuthenticationConfiguration authenticationConfiguration;
    // JWT処理ユーティリティBean
    private final JwtUtil jwtUtil;

    // bean登録
    // LoginFilterでユーザー認証を行う際に使用
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    // パスワード暗号化用Beanの登録
    // BCryptハッシュ関数を使用
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //filterChain設定
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT使用のためCSRF保護を無効化
                .csrf(AbstractHttpConfigurer::disable)

                // デフォルトのフォームログインおよびHTTP Basic認証を無効化
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // パス別の権限設定
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN") // 管理者権限専用パス
                        .anyRequest().permitAll())

                // フィルターの配置

                // JwtFilter：各リクエストごとにJWTトークンの有効性を検証
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // LoginFilter：「/login」リクエスト時にID・パスワードを受け取り認証を処理
                .addFilterAt(new LoginFilter(authenticationManager(), jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // セッションポリシー：STATELESS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}