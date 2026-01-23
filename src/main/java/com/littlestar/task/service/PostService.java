package com.littlestar.task.service;

import com.littlestar.task.domain.PostForm;

/** 投稿（ポスト）に関するビジネスロジックを管理するサービスインターフェース */
public interface PostService {

    /** 新しい投稿を作成します。*/
    void createPost(PostForm form, String boardName, String loginId);

    /**既存の投稿を更新（編集）します。*/
    void updatePost(PostForm form, String boardName, String loginId);

    /** 特定の投稿を削除します。*/
    void deletePost(Long postId, String loginId);
}