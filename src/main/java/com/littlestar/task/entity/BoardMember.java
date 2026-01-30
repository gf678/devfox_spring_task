package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 掲示板ごとのユーザー権限を管理するエンティティ
// 特定のユーザーが特定の掲示板でどの役割を持つかを記録
@Entity
@Getter @Setter
@NoArgsConstructor
public class BoardMember {

    // BoardMember識別子
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Userエンティティとの多対一(N:1)の関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Boardエンティティとの多対一(N:1)の関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 該当掲示板内での権限 (ADMIN, MODERATOR, USER)
    // EnumType.STRINGを使用してDBに文字列自体を保存
    @Enumerated(EnumType.STRING)
    private Role role;
}
