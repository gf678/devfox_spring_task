package com.littlestar.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// 投稿に付くコメント情報を管理するエンティティ
// 返信（リプライ）機能のために自己参照構造を含む
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    // コメントの固有識別番号
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    // Postとの多対一(N:1)関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false) // データベースの外部キー(FK)列名
    private Post post;

    // Userとの多対一(N:1)関係
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // データベースの外部キー(FK)列名
    private User user;

    // 自身との多対一(N:1)関係
    // 返信機能を実装するため親コメントのIDを保持
    // 通常コメントの場合、parent_idはnull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // コメント本文 (最大1000文字)
    @Column(nullable = false, length = 1000)
    private String content;

    // 削除フラグ (trueの場合は「削除されたコメントです」と表示)
    private Boolean isDeleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt; // コメント作成日時

    // エンティティが初めて保存されるときに実行
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
