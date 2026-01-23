package com.littlestar.task.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Board {

    @Id
    @Column(name = "board_id")
    private Long boardId;      // 掲示板識別番号 (PK)

    private String name;       // 掲示板の名前 (例: 自由掲示板、お知らせ)

    private String description; // 掲示板の説明

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 掲示板の作成日時
}