package com.littlestar.task.Controller;

import com.littlestar.task.Exception.BusinessException;
import com.littlestar.task.Exception.ErrorCode;
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

    @PostMapping("/{postId}/reaction")
    public ResponseEntity<Map<String, Integer>> addReaction(
            @PathVariable Long postId,
            @RequestParam String type,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED1);
        }

        Map<String, Integer> result =
                postService.updateReaction(postId, type, authentication.getName());

        return ResponseEntity.ok(result);
    }
}
