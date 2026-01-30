package com.littlestar.task.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

// 会員情報編集ページで入力されたデータを管理するDTO
@Data
public class UserEditForm {

    // ユーザーのニックネーム
    private String alias;

    // メールアドレス
    private String email;

    // 住所情報
    private String address;

    // パスワード
    private String password;

    // 電話番号
    private String phone;

    // 新しくアップロードするプロフィール画像ファイル
    private MultipartFile imageFile;

    // DBに保存されている既存画像のパスやファイル名
    private String profileImg;
}
