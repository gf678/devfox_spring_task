package com.littlestar.task.Controller;

import com.littlestar.task.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class LoginController {

    @Autowired
    private MailService mailService;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // 비밀번호 재설정 메일 요청
    @PostMapping("/password-reset")
    @ResponseBody
    public String requestReset(@RequestParam String email) {

        mailService.sendResetMail(email);

        return "パスワード再設定メールを送信しました。";
    }

    // 비밀번호 변경
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {

        model.addAttribute("token", token);

        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String password) {

        mailService.resetPassword(token, password);

        return "パスワードが変更されました。";
    }
}