package com.littlestar.task.Aspect;

import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Aspect
@Component
@RequiredArgsConstructor
public class PostPrivilegeAspect {

    private final PostRepository postRepository;

    @Before("execution(* com.littlestar.task.Controller.*Controller.delete*(..)) && args(boardName, postId, principal, authentication)")
    public void checkPrivilege(String boardName, Long postId, Principal principal, Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("記事は見つかりません。"));

        boolean isOwner = post.getUser().getLoginId().equals(principal.getName());
        boolean hasPrivilege = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MODERATOR"));

        if (!isOwner && !hasPrivilege) {
            throw new RuntimeException("削除権限がありません。");
        }
    }
}
