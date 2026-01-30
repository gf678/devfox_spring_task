package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// 掲示板情報を保存するエンティティ
@Entity
@Getter @Setter
@NoArgsConstructor
public class Board {

    // 掲示板識別子
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    // 掲示板名
    @Column(nullable = false, unique = true)
    private String name;

    // 掲示板の説明
    private String description;

    // 掲示板作成時間
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
