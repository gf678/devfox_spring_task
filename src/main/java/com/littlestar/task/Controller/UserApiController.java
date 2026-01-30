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
        try {
            // サービスでビジネスロジックを実行
            // - IDの重複チェック
            // - パスワードを暗号化して保存
            userService.signUp(form);
            return ResponseEntity.ok("SUCCESS");
        } catch (IllegalArgumentException e) {
            // 既存IDなどの論理エラーの場合 400返却
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // DBエラーなどサーバー内部エラーの場合 500返却
            return ResponseEntity.internalServerError().body("サーバーエラーが発生しました。");
        }
    }

    // ログイン処理 (JWT発行およびCookie保存)
    @PostMapping("/signIn")
    public String signIn(@RequestParam("id") String loginId,
                         @RequestParam("password") String password,
                         HttpServletResponse response) {

        // サービスでID/PWの一致確認とユーザー情報取得
        User user = userService.signIn(loginId, password);

        if (user != null) {
            // 認証成功時にJWTトークンを生成 (ID, 権限, 有効期限1時間)
            String token = jwtUtil.createJwt(user.getLoginId(), user.getRole().name(), 60 * 60 * 1000L);

            // 生成したトークンをCookieに保存してクライアントに送信
            Cookie cookie = new Cookie("Authorization", token);
            cookie.setHttpOnly(true); // JSからアクセス不可、XSS対策
            cookie.setPath("/");      // 全てのパスで使用可能
            cookie.setMaxAge(60 * 60); // 1時間

            response.addCookie(cookie);

            // ログイン成功後、メインページへリダイレクト
            return "redirect:/";
        } else {
            // 認証失敗時、エラーパラメータ付きでログインページへリダイレクト
            return "redirect:/login?error=true";
        }
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
