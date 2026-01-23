package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId; // 投稿識別番号 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 作成者 (FK) - N:1関係

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board; // 所属掲示板 (FK) - N:1関係

    @Column(nullable = false, length = 200)
    private String title; // 投稿タイトル

    @Column(columnDefinition = "TEXT")
    private String content; // 投稿内容

    @Column(nullable = false)
    private int views = 0;    // 照会数 (デフォルト 0)

    @Column(nullable = false)
    private int likes = 0;    // いいね数 (デフォルト 0)

    @Column(nullable = false)
    private int dislikes = 0; // よくないね数 (デフォルト 0)

    // コメントリストとの1:N関係設定
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    // -----------------------

    @Column(updatable = false)
    private LocalDateTime createdAt; // 作成日時

    // DBに保存される直前に現在時刻を記録
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}