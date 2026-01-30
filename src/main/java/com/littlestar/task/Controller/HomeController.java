package com.littlestar.task.Controller;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// メイン画面用コントローラー
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;

    // ルートメソッド
    @GetMapping("/")
    public String home(Model model) {

        // すべての掲示板を取得
        List<Board> boards = boardRepository.findAll();

        // 各掲示板名をキー、最新投稿上位10件を値とするMapを作成
        Map<String, List<Post>> boardMap = new HashMap<>();
        for (Board b : boards) {
            boardMap.put(b.getName(), postRepository.findTop10ByBoardOrderByCreatedAtDesc(b));
        }

        // サービス全体で人気のある投稿（Likes > 0）の上位10件を取得
        List<Post> popularPosts = postRepository.findTop10ByLikesGreaterThanOrderByLikesDesc(0);

        // モデルにデータをバインド
        model.addAttribute("boards", boards);           // 全掲示板一覧
        model.addAttribute("boardMap", boardMap);       // 各掲示板別最新投稿
        model.addAttribute("popularPosts", popularPosts); // 人気投稿TOP10

        // Thymeleafレイアウト内で本文に挿入するHTMLフラグメントを設定
        model.addAttribute("content", "contents/home :: home");

        // 共通レイアウトページを返却（layout.html内でcontentフラグメントをレンダリング）
        return "layout";
    }
}
