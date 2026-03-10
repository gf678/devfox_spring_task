package com.littlestar.task.Aspect;

import com.littlestar.task.entity.Post;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Aspect
@Component
public class PostDeleteAuthAspect {

    @Before("execution(* com.littlestar.task.Controller.PostController.deletePost(..)) && args(.., principal, auth)")
    public void checkDeleteAuth(Principal principal, Authentication auth, JoinPoint jp) {
        Post post = (Post) jp.getArgs()[1]; // postId 대신 Post 객체를 미리 매핑하면 좋음
        boolean isOwner = post.getUser().getLoginId().equals(principal.getName());
        boolean hasPrivilege = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MODERATOR"));
        if (!isOwner && !hasPrivilege) throw new RuntimeException("삭제 권한 없음");
    }
}
