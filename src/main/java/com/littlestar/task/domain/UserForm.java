package com.littlestar.task.domain;

import lombok.Data;

// 会員登録時にユーザーから入力されたデータを保持するDTO
@Data
public class UserForm {

    // ログイン時に使用するユーザーID（ユニーク識別子）
    private String loginId;

    // サービス内で表示されるユーザーのニックネーム
    private String alias;

    // アカウントのパスワード（サービス層で必ず暗号化して保存する必要があります）
    private String password;

    // 連絡および認証用のメールアドレス
    private String email;

    // ユーザー権限（USER, ADMIN, MORDERATOR）
    private String role;
}
