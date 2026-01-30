package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// システムのユーザー（会員）情報を管理するエンティティ

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 無分別なオブジェクト生成を防ぐためアクセス制御
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id") // オブジェクト比較時はid値のみを基準に比較

public class User {

    // 会員固有番号 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    // ログイン用ID
    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    // 暗号化されたパスワード (ハッシュ値が保存されるため長さは255に設定)
    @Column(name = "passwd", nullable = false, length = 255)
    private String password;

    // サービス内で表示されるニックネーム
    @Column(name = "alias", nullable = false, unique = true, length = 50)
    private String alias;

    // メールアドレス (重複不可)
    @Column(unique = true, length = 100)
    private String email;

    // サーバーに保存されたプロフィール画像のパスまたはURL
    @Column(name = "profile_image")
    private String profileImg;

    private String address; // 住所
    private String phone;   // 電話番号

    // 登録日時
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 権限レベル (ADMIN, MODERATOR, USER)
    @Enumerated(EnumType.STRING)
    private Role role;

    // データベースに初めてデータが保存される際に実行
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
