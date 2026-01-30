package com.littlestar.task.service;

// コメントおよび返信（リプライ）に関するビジネスロジックを処理するサービスインターフェース
public interface CommentService {

    // 新しいコメントまたは返信を保存
    // 通常のコメントの場合は null、特定のコメントへの返信（リプライ）の場合はそのコメントのIDを渡す
    void saveComment(Long postId, String content, String loginId, Long parentId);
}
