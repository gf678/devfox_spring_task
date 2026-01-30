package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

// 投稿に添付された画像情報を管理するエンティティ
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostImage {

    // 画像識別番号 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    // Postとの多対一(N:1)関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 外部キー(FK)

    // 保存された画像ファイルのパスまたはURL
    @Column(nullable = false)
    private String imageUrl;

    // 投稿内で画像が表示される順序
    @Column(nullable = false)
    private Integer sortOrder;

    // 画像登録日時
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // エンティティ保存前に実行されるメソッド
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
