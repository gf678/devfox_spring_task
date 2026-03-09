package com.littlestar.task.service;

import com.littlestar.task.entity.PasswordResetToken;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.PasswordResetTokenRepository;
import com.littlestar.task.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender; // メール送信を行うSpringのメール送信クラス

    @Autowired
    private UserRepository userRepository; // ユーザー情報を取得するRepository

    @Autowired
    private PasswordResetTokenRepository tokenRepository; // パスワード再設定トークン管理Repository

    @Autowired
    private PasswordEncoder passwordEncoder; // パスワード暗号化用

    /**
     * パスワード再設定メール送信
     */
    @Override
    @Transactional
    public void sendResetMail(String email) {

        // メールアドレスでユーザー検索
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません。"));

        // ランダムトークン生成
        String token = UUID.randomUUID().toString();

        // 既存トークンがあれば取得、なければ新規作成
        PasswordResetToken resetToken =
                tokenRepository.findByUser(user).orElse(new PasswordResetToken());

        // トークン情報設定
        resetToken.setUser(user);
        resetToken.setToken(token);

        // トークン有効期限（30分）
        resetToken.setExpireDate(LocalDateTime.now().plusMinutes(30));

        // DB保存
        tokenRepository.save(resetToken);

        // パスワード再設定リンク生成
        String link = "http://deer2922.ddns.net:8080/reset-password?token=" + token;

        // メール作成
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("パスワード再設定をリクエストしました。");
        message.setText("パスワード再設定用リンク:\n" + link);

        // メール送信
        mailSender.send(message);
    }

    /**
     * トークン検証後、パスワード変更
     */
    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        // トークン検索
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("無効なトークンです。"));

        // トークン有効期限チェック
        if (resetToken.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("トークンの有効期限が切れました。");
        }

        // トークンに紐づくユーザー取得
        User user = resetToken.getUser();

        // 新しいパスワードを暗号化して保存
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 使用済みトークン削除（再利用防止）
        tokenRepository.delete(resetToken);
    }
}