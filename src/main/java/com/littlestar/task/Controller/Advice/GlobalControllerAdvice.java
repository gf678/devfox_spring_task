package com.littlestar.task.Controller.Advice;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import com.littlestar.task.entity.Subscription;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.repository.SubscriptionRepository;
import com.littlestar.task.repository.UserRepository;
import com.littlestar.task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// グローバルデータ設定用
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    // すべての掲示板データを返却
    @ModelAttribute("allBoards")
    public List<Board> allBoards() {
        return boardRepository.findAll();
    }

    // 現在のユーザーが購読している掲示板データを返却
    @ModelAttribute("subscribedBoards")
    public List<Board> subscribedBoards(Principal principal) {
        // 未ログインの場合は空のリストを返却
        if (principal == null) {
            return new ArrayList<>();
        }

        // 現在ログイン中のIDでユーザーを取得
        return userRepository.findByLoginId(principal.getName())
                .map(user -> {
                    // 해당 유저의 모든 구독 내역 조회
                    List<Subscription> subscriptions = subscriptionRepository.findAllByUser(user);

                    // 구독 내역에서 Board 엔티티만 추출(Stream API 활용)
                    return subscriptions.stream()
                            .map(Subscription::getBoard)
                            .collect(Collectors.toList());
                })
                .orElse(new ArrayList<>()); // 유저를 찾을 수 없는 경우 빈 리스트 반환
    }

    // すべての投稿一覧を返却
    @ModelAttribute("allPosts")
    public List<Post> allPosts() {
        return postRepository.findAll();
    }

    // ログイン中ユーザーのレコードを返却
    @ModelAttribute
    public void addAttributes(Principal principal, Model model) {
        if (principal != null) {
            // ログイン状態の場合のみDBからユーザー情報を取得してモデルに格納
            User user = userService.findByLoginId(principal.getName());
            model.addAttribute("user", user);
        }
    }
}