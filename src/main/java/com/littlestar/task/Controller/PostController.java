package com.littlestar.task.Controller;

import com.littlestar.task.domain.PostForm;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final BoardRepository boardRepository;

    //create
    // 1. 新規投稿の保存
    @PostMapping("/board/{boardName}/save")
    public String createPost(@PathVariable String boardName,
                             @ModelAttribute("post") PostForm form,
                             Principal principal) {
        // ログインしていない場合はテストユーザーを使用
        String loginId = (principal != null) ? principal.getName() : "test_user";
        postService.createPost(form, boardName, loginId);
        return "redirect:/board/" + boardName + "/list";
    }
    //update
    // 2. 既存投稿の修正
    @PostMapping("/board/{boardName}/update/{postId}")
    public String updatePost(@PathVariable String boardName,
                             @PathVariable Long postId,
                             @ModelAttribute("post") PostForm form,
                             Principal principal) {
        String loginId = (principal != null) ? principal.getName() : "test_user";
        form.setPostId(postId); // フォームに投稿IDを手動で設定
        postService.updatePost(form, boardName, loginId);
        return "redirect:/board/" + boardName + "/post/" + postId;
    }

    //delete
    // 3. 投稿の削除
    @PostMapping("/board/{boardName}/delete/{postId}")
    public String deletePost(@PathVariable String boardName,
                             @PathVariable Long postId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        String loginId = (principal != null) ? principal.getName() : "test_user";

        try {
            // 投稿IDとログインIDをサービス層に渡して削除を実行
            postService.deletePost(postId, loginId);
            redirectAttributes.addFlashAttribute("message", "投稿が削除されました。");
        } catch (Exception e) {
            // 権限がない場合などはエラーを付けて詳細ページへ戻る
            return "redirect:/board/" + boardName + "/post/" + postId + "?error";
        }

        // 削除成功後はリスト画面へリダイレクト
        return "redirect:/board/" + boardName + "/list";
    }
}