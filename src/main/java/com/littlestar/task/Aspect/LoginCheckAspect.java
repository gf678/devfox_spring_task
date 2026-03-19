package com.littlestar.task.Aspect;

import com.littlestar.task.Exception.BusinessException;
import com.littlestar.task.Exception.ErrorCode;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Aspect
@Component
public class LoginCheckAspect {

    @Before("execution(* com.littlestar.task.Controller.*Controller.*(..)) && args(.., principal)")
    public void checkLogin(Principal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED1);
        }
    }
}
