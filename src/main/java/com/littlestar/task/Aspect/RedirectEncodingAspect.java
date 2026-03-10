package com.littlestar.task.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Aspect
@Component
public class RedirectEncodingAspect {

    @Around("execution(* com.littlestar.task.Controller.*Controller.*(..))")
    public Object encodeRedirectUrl(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof String str && str.startsWith("redirect:")) {
            String url = str.substring("redirect:".length());

            // ルート (/) または既に安全なパスはエンコードしない
            if (!url.startsWith("/")) {
                url = URLEncoder.encode(url, StandardCharsets.UTF_8);
            }

            return "redirect:" + url;
        }

        return result;
    }
}