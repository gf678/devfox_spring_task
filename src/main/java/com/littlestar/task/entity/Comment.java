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
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId; // コメント識別番号 (PK)

    // 1. Post（投稿）との多対一関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false) // DBの外部キーカラム名
    private Post post;

    // 2. User（ユーザー）との多対一関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // DBの外部キーカラム名
    private User user;

    // 3. 親コメント（自己参照）との多対一関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // 大コメント・返信機能用
    private Comment parent;

    @Column(nullable = false, length = 1000)
    private String content; // コメント内容

    private Boolean isDeleted = false; // 削除有無 (初期値false)

    @Column(updatable = false)
    private LocalDateTime createdAt; // 作成日

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
