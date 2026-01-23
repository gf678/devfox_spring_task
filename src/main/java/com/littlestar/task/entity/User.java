package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id; // 会員固有番号 (PK)

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId; // ログインID

    @Column(name = "passwd", nullable = false, length = 255)
    private String password; // パスワード

    @Column(name = "alias", nullable = false, unique = true, length = 50)
    private String nickname; // ニックネーム (別名)

    @Column(unique = true, length = 100)
    private String email; // メールアドレス

    @Column(name = "profile_image")
    private String profileImg; // プロフィール画像パス

    private String address; // 住所
    private String phone; // 電話番号

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 加入日

    // DBにデータが挿入される際、時間を自動的に記録
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}