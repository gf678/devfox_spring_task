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
    public void addComment(@PathVariable Long postId,
                           @RequestBody Map<String, String> payload,
                           Authentication authentication) {

        String content = payload.get("content");
        Long parentId = payload.get("parentId") != null ? Long.valueOf(payload.get("parentId")) : null;

        // 비즈니스 로직 호출
        commentService.saveComment(postId, content, authentication.getName(), parentId);
    }

    @PostMapping("/{postId}/comments/update/{id}")
    @ResponseBody
    public String updateComment(@PathVariable Long id,
                                @RequestBody Map<String,String> data) {

        commentService.updateComment(id, data.get("content"));
        return "ok";
    }

    @PostMapping("/{postId}/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id) {

        commentService.deleteComment(id);
        return "ok";
    }
}
