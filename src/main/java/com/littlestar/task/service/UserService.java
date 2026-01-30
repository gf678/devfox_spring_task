package com.littlestar.task.service;

import com.littlestar.task.domain.UserEditForm;
import com.littlestar.task.domain.UserForm;
import com.littlestar.task.entity.User;

// ユーザー（会員）に関するビジネスロジックを処理するサービスインターフェース
public interface UserService {

    // 新しいユーザーを登録（会員登録）
    void signUp(UserForm form);

    // ログインIDとパスワードを検証してログインを処理
    User signIn(String loginId, String password);

    // 既存ユーザーの個人情報（パスワード、ニックネーム、プロフィール画像など）を更新
    void updateUserInfo(String loginId, UserEditForm form);

    // ログインIDを基にユーザーの詳細情報を取得
    User findByLoginId(String loginId);
}
