package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId; // 画像識別番号 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 投稿識別番号 (FK)

    @Column(nullable = false)
    private String imageUrl; // 画像URL

    @Column(nullable = false)
    private Integer sortOrder; // 表示順序

    @Column(updatable = false)
    private LocalDateTime createdAt; // 作成日時
}