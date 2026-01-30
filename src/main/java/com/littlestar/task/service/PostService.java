package com.littlestar.task.service;

import com.littlestar.task.domain.PostForm;
import java.util.Map;

// 投稿(Post)に関連するビジネスロジックを処理するサービスインターフェース
public interface PostService {

    // 新しい投稿を作成
    void createPost(PostForm form, String boardName, String loginId);

    // 既存の投稿を更新
    void updatePost(PostForm form, String boardName, String loginId);

    // 特定の投稿を削除
    void deletePost(Long postId, String loginId);

    // 投稿に対するリアクションを更新
    Map<String, Integer> updateReaction(Long postId, String type, String loginId);
}
