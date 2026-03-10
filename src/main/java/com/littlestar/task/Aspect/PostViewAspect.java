package com.littlestar.task.Aspect;

import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.security.Principal;


@Aspect
@Component
@RequiredArgsConstructor
public class PostViewAspect {

    private final PostRepository postRepository;

    @AfterReturning(pointcut = "execution(* com.littlestar.task.Controller.BoardController.postView(..))", returning = "retVal")
    public void increaseViews(JoinPoint joinPoint, Object retVal) {
        Object[] args = joinPoint.getArgs();
        Long postId = (Long) args[1];
        Principal principal = (Principal) args[3];

        Post post = postRepository.findById(postId).orElseThrow();
        post.setViews(post.getViews() + 1);
        postRepository.save(post);

        String user = (principal != null) ? principal.getName() : "anonymous";
        System.out.println("[閲覧履歴] user=" + user + ", postId=" + postId);
    }
}
