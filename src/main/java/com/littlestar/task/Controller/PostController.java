package com.littlestar.task.Controller;

import com.littlestar.task.domain.PostForm;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

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

        String loginId = principal.getName();

        // サービス層に保存処理を委譲
        postService.createPost(form, boardName, loginId);

        // リダイレクト（AOPで自動エンコードされる）
        return "redirect:/board/" + boardName + "/list";
    }

    // 投稿の編集
    @PostMapping("/board/{boardName}/update/{postId}")
    public String updatePost(@PathVariable String boardName,
                             @PathVariable Long postId,
                             @ModelAttribute("post") PostForm form,
                             Principal principal) {

        form.setPostId(postId);

        postService.updatePost(form, boardName, principal.getName());

        return "redirect:/board/" + boardName + "/list";
    }

    // 投稿の削除
    @PostMapping("/board/{boardName}/delete/{postId}")
    public String deletePost(@PathVariable String boardName,
                             @PathVariable Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("投稿が見つかりません。"));

        postRepository.delete(post);

        return "redirect:/board/" + boardName + "/list";
    }
}