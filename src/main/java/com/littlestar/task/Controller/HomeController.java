package com.littlestar.task.Controller;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;

    @GetMapping("/")
    public String home(Model model) {
        // 1. すべての掲示板を取得。データがない場合に備えて件数を確認。
        List<Board> boards = boardRepository.findAll();

        // 2. 各掲示板ごとの最新投稿10件を格納するマップを作成
        Map<String, List<Post>> boardMap = new HashMap<>();
        for (Board b : boards) {
            if (b != null && b.getName() != null) { // nullチェックを追加
                // 掲示板オブジェクトを基準に最新の10件を取得
                List<Post> topPosts = postRepository.findTop10ByBoardOrderByCreatedAtDesc(b);
                boardMap.put(b.getName(), topPosts);
            }
        }

        // 3. ビュー(Thymeleaf)にデータを渡す
        model.addAttribute("boards", boards);
        model.addAttribute("boardMap", boardMap);
        model.addAttribute("content", "contents/home :: home"); // メ인コンテンツのパス

        return "layout"; // 共通レイアウトを適用
    }
}