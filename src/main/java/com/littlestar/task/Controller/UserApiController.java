package com.littlestar.task.Controller;

import com.littlestar.task.domain.UserForm;
import com.littlestar.task.security.JwtUtil;
import com.littlestar.task.entity.User;
import com.littlestar.task.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// ユーザー認証および会員管理を処理するAPIコントローラー
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserApiController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 会員登録処理
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(UserForm form) {
        userService.signUp(form);
        return ResponseEntity.ok("SUCCESS");
    }

    // ログイン処理 (JWT発行およびCookie保存)
    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestParam String id,
                                         @RequestParam String password,
                                         HttpServletResponse response) {

        User user = userService.signIn(id, password);

        String token = jwtUtil.createJwt(user.getLoginId(), user.getRole().name(), 60 * 60 * 1000L);

        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok("SUCCESS");
    }

    // ログアウト処理 (Cookie削除)
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {

        // 同名Cookieを作成し、有効期限を0に → クライアントCookieを即時削除
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        // ログアウト後、メインページへリダイレクト
        return "redirect:/";
    }
}
