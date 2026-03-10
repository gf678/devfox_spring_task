package com.littlestar.task.Controller;

import com.littlestar.task.domain.PostForm;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Objects;

// 投稿の作成(Create)、編集(Update)、削除(Delete)リクエストを処理するコントローラー
@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;

    // 新規投稿の保存
    @PostMapping("/board/{boardName}/save")
    public String createPost(@PathVariable String boardName,
                             @ModelAttribute("post") PostForm form,
                             Principal principal) {


        // ログイン中のユーザーIDを取得
        String loginId = principal.getName();

        // サービス層に保存処理を委譲
        postService.createPost(form, boardName, loginId);

        // 保存後、掲示板一覧ページにリダイレクト（日本語対応）
        String encodedBoardName = URLEncoder.encode(boardName, StandardCharsets.UTF_8);
        return "redirect:/board/" + encodedBoardName + "/list";
    }

    // 投稿の編集
    @PostMapping("/board/{boardName}/update/{postId}")
    public String updatePost(@PathVariable String boardName,
                             @PathVariable Long postId,
                             @ModelAttribute("post") PostForm form,
                             Principal principal) {

        // パス変数 postId をフォームオブジェクトに設定
        form.setPostId(postId);

        // サービス層に編集処理を委譲
        postService.updatePost(form, boardName, principal.getName());

        // 編集完了後、投稿詳細ページにリダイレクト
        String encodedBoardName = URLEncoder.encode(boardName, StandardCharsets.UTF_8);
        return "redirect:/board/" + encodedBoardName + "/list";
    }

    // 投稿の削除（権限チェックあり）
    @PostMapping("/board/{boardName}/delete/{postId}")
    @Transactional
    public String deletePost(@PathVariable String boardName,
                             @PathVariable Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("投稿が見つかりません。"));

        postRepository.delete(post);

        String encodedBoardName = URLEncoder.encode(boardName, StandardCharsets.UTF_8);
        return "redirect:/board/" + encodedBoardName + "/list";
    }
}