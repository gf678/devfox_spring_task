package com.littlestar.task.Controller;

import com.littlestar.task.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 投稿のいいね/よくないね機能を処理するコントローラー
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostReactionController {

    private final PostService postService;

    // 投稿にいいねまたはよくないねを追加/取消
    @PostMapping("/{postId}/reaction")
    public ResponseEntity<?> addReaction(@PathVariable Long postId,
                                         @RequestParam String type,
                                         Authentication authentication) {

        // ログインしていないユーザーは反応を残せない
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ログインが必要です。");
        }

        try {
            // サービス層でビジネスロジックを処理
            // - ユーザーが既に反応しているか確認
            // - 既存の反応を取り消す、または変更
            // - 最新のいいね/よくないね数を返却
            Map<String, Integer> result = postService.updateReaction(postId, type, authentication.getName());

            // 成功時はHTTP 200とともに結果データを返却
            return ResponseEntity.ok(result);

        } catch (IllegalStateException e) {
            // 同じ反応を連続で押した場合や、許可されていないタイプの場合
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // DBエラーなど予期しない例外が発生した場合
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("処理中にエラーが発生しました。");
        }
    }
}
