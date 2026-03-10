package com.littlestar.task.Aspect;

import com.littlestar.task.entity.Comment;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.CommentRepository;
import com.littlestar.task.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipAspect {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Before("execution(* com.littlestar.task.Controller.*Controller.*(..)) && args(.., principal, auth)")
    public void checkOwnership(Principal principal, Authentication auth, JoinPoint jp) {

        Object[] args = jp.getArgs();

        for (Object arg : args) {
            if (arg instanceof Long id) {

                // Post 체크
                postRepository.findById(id).ifPresent(post -> {
                    boolean isOwner = post.getUser().getLoginId().equals(principal.getName());
                    boolean hasPrivilege = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MODERATOR"));
                    if (!isOwner && !hasPrivilege) {
                        throw new RuntimeException("投稿の修正／削除権限がありません。");
                    }
                });

                // Comment 체크
                commentRepository.findById(id).ifPresent(comment -> {
                    boolean isOwner = comment.getUser().getLoginId().equals(principal.getName());
                    boolean hasPrivilege = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MODERATOR"));
                    if (!isOwner && !hasPrivilege) {
                        throw new RuntimeException("コメントの修正／削除権限がありません。");
                    }
                });
            }
        }
    }
}