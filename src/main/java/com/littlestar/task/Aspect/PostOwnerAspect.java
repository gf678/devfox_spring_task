package com.littlestar.task.Aspect;

import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Aspect
@Component
@RequiredArgsConstructor
public class PostOwnerAspect {

    private final PostRepository postRepository;

    @Before("execution(* com.littlestar.task.Controller.*Controller.update*(..)) && args(boardName, postId, .., principal)")
    public void checkOwner(String boardName, Long postId, Principal principal) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("記事は見つかりません。"));
        if (!post.getUser().getLoginId().equals(principal.getName())) {
            throw new RuntimeException("作成者だけがアクセスできます。");
        }
    }
}
