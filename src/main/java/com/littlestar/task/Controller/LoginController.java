package com.littlestar.task.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // ログインページを返すメソッド
    // GETリクエスト時に auth/login.html ビューをレンダリング
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}