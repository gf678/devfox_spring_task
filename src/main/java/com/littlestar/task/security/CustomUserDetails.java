package com.littlestar.task.security;

import com.littlestar.task.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// Spring Securityがユーザーの認証情報を保持するために使用するオブジェクト
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user; // 実際にDBから取得したユーザーエンティティ

    // ユーザーが持つ権限(Role)の一覧を返す
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    // パスワードを返す
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // ログイン時に使用するIDを返す
    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    // ユーザーのプロフィール画像を返す
    public String getProfileImg() {
        return user.getProfileImg();
    }

    // ユーザーの固有ID(PK)を返す
    public Long getUserId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // アカウントの有効期限
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // アカウントのロック状態
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 資格情報(パスワード)の有効期限
    }

    @Override
    public boolean isEnabled() {
        return true; // アカウントの有効/無効
    }
}
