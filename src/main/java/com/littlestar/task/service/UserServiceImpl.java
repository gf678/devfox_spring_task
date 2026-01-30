package com.littlestar.task.service;

import com.littlestar.task.domain.UserEditForm;
import com.littlestar.task.domain.UserForm;
import com.littlestar.task.entity.Role;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.UserRepository;
import com.littlestar.task.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    // ユーザー情報を登録
    @Override
    public void signUp(UserForm form) {
        // 既に存在するIDか確認
        validateDuplicateUsername(form.getLoginId());

        // Entity変換およびパスワード暗号化保存
        User user = User.builder()
                .loginId(form.getLoginId())
                .password(bCryptPasswordEncoder.encode(form.getPassword())) // セキュリティのため必ず暗号化
                .alias(form.getAlias())
                .role(Role.USER) // デフォルト権限付与
                .email(form.getEmail())
                .build();

        // DB保存
        userRepository.save(user);
    }

    // ID重複チェックロジック
    private void validateDuplicateUsername(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("すでに使用されているIDです。");
        }
    }

    // ログイン認証処理
    @Override
    public User signIn(String loginId, String password) {
        // IDでユーザー情報を取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("存在しないIDです。"));

        // パスワード一致確認: BCryptは単方向暗号化のため matches メソッドで比較
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("パスワードが一致しません。");
        }

        return user;
    }

    // ユーザー情報更新および現在のログインセッション同期
    @Override
    @Transactional
    public void updateUserInfo(String loginId, UserEditForm form) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("ユーザーを見つけられません。"));

        // プロフィール画像更新: 新しいファイルがある場合のみ既存パスを置換
        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            String savedPath = imageService.saveImage(form.getImageFile());
            user.setProfileImg(savedPath);
        }

        // 基本情報変更
        user.setAlias(form.getAlias());
        user.setEmail(form.getEmail());
        user.setAddress(form.getAddress());
        user.setPhone(form.getPhone());

        // パスワード変更ロジック: 新しいパスワードが入力されている場合のみ暗号化して保存
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
            log.info("パスワード変更完了");
        } else {
            log.info("既存のパスワードを維持");
        }

        // DBだけが変更された場合、現在ログイン中の情報(Context)は古い状態なので手動で更新
        updateSecurityContext(user);
    }

    // Spring Securityコンテキスト内の認証情報を最新状態に更新
    private void updateSecurityContext(User user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 現在のユーザーエンティティを含む新しいUserDetails生成
        CustomUserDetails newUserDetails = new CustomUserDetails(user);

        // 新しい認証トークン生成
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                Objects.requireNonNull(auth).getCredentials(),
                newUserDetails.getAuthorities()
        );

        // セキュリティコンテキストに新しい認証情報を設定
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @Override
    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("該当するユーザーが見つかりません: " + loginId));
    }
}
