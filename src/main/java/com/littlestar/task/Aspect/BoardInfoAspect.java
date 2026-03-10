package com.littlestar.task.Aspect;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.repository.SubscriptionRepository;
import com.littlestar.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.security.Principal;

import com.littlestar.task.domain.PostForm;
import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.repository.SubscriptionRepository;
import com.littlestar.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.security.Principal;

@Aspect
@Component
@RequiredArgsConstructor
public class BoardInfoAspect {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Before("execution(* com.littlestar.task.Controller.BoardController.*(..)) && args(boardName, .., model, principal)")
    public void injectBoardInfo(String boardName, Model model, Principal principal) {
        Board board = boardRepository.findByName(boardName)
                .orElseThrow(() -> new RuntimeException("掲示板が見つかりません： " + boardName));

        boolean isSubscribed = false;
        if (principal != null) {
            isSubscribed = userRepository.findByLoginId(principal.getName())
                    .map(user -> subscriptionRepository.existsByUserAndBoard(user, board))
                    .orElse(false);
        }

        model.addAttribute("boardName", boardName);
        model.addAttribute("boardId", board.getBoardId());
        model.addAttribute("description", board.getDescription());
        model.addAttribute("isSubscribed", isSubscribed);
        model.addAttribute("content", "contents/board/board :: board");
    }
}
