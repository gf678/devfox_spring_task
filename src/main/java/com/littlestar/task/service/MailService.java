package com.littlestar.task.service;

public interface MailService {

    // パスワード再設定メール送信
    void sendResetMail(String email);

    // トークン検証後にパスワードを変更
    void resetPassword(String token, String newPassword);

}
