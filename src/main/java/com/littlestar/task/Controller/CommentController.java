package com.littlestar.task.Controller;

import com.littlestar.task.service.CommentServiceImpl; // 댓글 비즈니스 로직을 처리하는 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServiceImpl commentService; // コメントのビジネスロジックを処理するサービス

    // コメント作成API
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long postId,
                                             @RequestBody Map<String, String> payload,
                                             Authentication authentication) {

        // ログイン状態を確認
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログインが必要です。");
        }

        // コメント内容を取得
        String content = payload.get("content");

        // 大コメント（返信コメント）かどうかを判定
        Long parentId = payload.get("parentId") != null ? Long.valueOf(payload.get("parentId")) : null;

        // バリデーション（内容が空の場合はエラー返却）
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("内容を入力してください。");
        }

        try {
            // ビジネスロジック呼び出し（投稿ID、内容、ユーザー名、親コメントIDを渡す）
            commentService.saveComment(postId, content, authentication.getName(), parentId);

            // 成功時はクライアントに success を返す
            return ResponseEntity.ok("success");

        } catch (Exception e) {
            // 500 エラー時の例外処理
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("保存中にエラーが発生しました: " + e.getMessage());
        }
    }
}
