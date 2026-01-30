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

        // ログイン状態を確認
        if (principal == null) {
            return "redirect:/user/login"; // 未ログインの場合はログインページへリダイレクト
        }

        // ログイン中のユーザーIDを取得
        String loginId = principal.getName();

        // サービス層に保存処理を委譲
        postService.createPost(form, boardName, loginId);

        // 保存後、掲示板一覧ページにリダイレクト
        return "redirect:/board/" + boardName + "/list";
    }

    // 投稿の編集
    @PostMapping("/board/{boardName}/update/{postId}")
    public String updatePost(@PathVariable String boardName,
                             @PathVariable Long postId,
                             @ModelAttribute("post") PostForm form,
                             Principal principal) {

        String loginId = (principal != null) ? principal.getName() : "test_user";

        // パス変数 postId をフォームオブジェクトに設定
        form.setPostId(postId);

        // サービス層に編集処理を委譲
        postService.updatePost(form, boardName, loginId);

        // 編集完了後、投稿詳細ページにリダイレクト
        return "redirect:/board/" + boardName + "/post/" + postId;
    }

    // 投稿の削除（権限チェックあり）
    @PostMapping("/board/{boardName}/delete/{postId}")
    @Transactional
    public String deletePost(@PathVariable String boardName,
                             @PathVariable Long postId,
                             Principal principal,
                             Authentication authentication) {

        // DBから対象投稿を取得
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("投稿が見つかりません。"));

        // 未ログインの場合はログインページへリダイレクト
        if (principal == null) return "redirect:/login";

        // 投稿者本人か確認
        boolean isOwner = post.getUser().getLoginId().equals(principal.getName());

        // 管理者(ADMIN)またはモデレーター(MODERATOR)権限を確認
        boolean hasPrivilege = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.requireNonNull(a.getAuthority()).equals("ADMIN") ||
                        a.getAuthority().equals("MODERATOR"));

        // 本人でもなく権限もない場合は例外
        if (!isOwner && !hasPrivilege) {
            throw new RuntimeException("削除権限がありません。");
        }

        // 投稿を削除
        postRepository.delete(post);

        // 削除後、掲示板一覧ページにリダイレクト
        return "redirect:/board/" + boardName + "/list";
    }
}