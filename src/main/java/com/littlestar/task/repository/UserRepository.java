package com.littlestar.task.repository;

import com.littlestar.task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //ログインIDでユーザーを照会します。(ログイン処理および投稿作成時のユーザー確認用)
    Optional<User> findByLoginId(String loginId);

    //メールアドレスでユーザーを照会します。(パスワード再設定や重複加入の防止用)
    Optional<User> findByEmail(String email);

    //重複確認のための存在有無チェック (会員登録時に活用)
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);

    //ニックネーム(alias)でユーザーを照会します。
    Optional<User> findByNickname(String nickname);
}