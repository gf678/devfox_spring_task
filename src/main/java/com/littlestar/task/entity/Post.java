package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 投稿情報を管理するエンティティです。
 * 作成者、掲示板、コメント、リアクションと有機的に連携しています。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {

    // 投稿の識別番号
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    // Userとの多対一(N:1)関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 外部キー(FK)

    // Boardとの多対一(N:1)関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board; // 外部キー(FK)

    // 投稿タイトル
    @Column(nullable = false, length = 200)
    private String title;

    // 投稿本文
    @Column(columnDefinition = "TEXT")
    private String content;

    // 閲覧数 (デフォルト 0)
    @Column(nullable = false)
    private int views = 0;

    // いいね数 (デフォルト 0)
    @Column(nullable = false)
    private int likes = 0;

    // 低評価数 (デフォルト 0)
    @Column(nullable = false)
    private int dislikes = 0;

    // Commentとの一対多(1:N)関係
    // 投稿が削除されると、該当投稿のコメントも一緒に削除される(CascadeType.ALL, orphanRemoval)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // PostReactionとの一対多(1:N)関係
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReaction> reactions = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt; // 作成日時

    // データベースに保存(Persist)される直前に実行
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
