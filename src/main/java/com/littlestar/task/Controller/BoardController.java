package com.littlestar.task.Controller;

import com.littlestar.task.Exception.BusinessException;
import com.littlestar.task.Exception.ErrorCode;
import com.littlestar.task.domain.PostForm;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/board/{boardName}")
@RequiredArgsConstructor
public class BoardController {

    private final PostRepository postRepository;

    // 掲示板の投稿一覧表示
    @GetMapping("/list")
    public String boardList(@PathVariable String boardName,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "15") int size,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "sort", required = false) String sort,
                            Model model, Principal principal) {

        // ページング設定：作成日で降順ソート
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> paging;

        // 人気投稿の処理
        if ("popular".equals(sort)) {
            if (keyword != null && !keyword.isBlank()) {
                // キーワード検索付き人気投稿
                paging = postRepository.findByBoard_NameAndLikesGreaterThanEqualAndTitleContainingOrderByCreatedAtDesc(
                        boardName, 10, keyword, pageable);
            } else {
                // デフォルト人気投稿
                paging = postRepository.findByBoard_NameAndLikesGreaterThanEqualOrderByCreatedAtDesc(
                        boardName, 10, pageable);
            }
        }
        // 一般投稿の処理
        else if (keyword != null && !keyword.isBlank()) {
            // キーワード検索付き一般投稿
            paging = postRepository.findByBoard_NameAndTitleContainingOrderByCreatedAtDesc(boardName, keyword, pageable);
        } else {
            // デフォルト一般投稿
            paging = postRepository.findByBoard_NameOrderByCreatedAtDesc(boardName, pageable);
        }

        // モデルに共通データを追加
        model.addAttribute("paging", paging);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("boardContent", "post-list"); // 表示用フラグメント

        return "layout";
    }

    // 投稿詳細表示
    @GetMapping("/post/{postId}")
    public String postView(@PathVariable String boardName, @PathVariable Long postId,
                           Model model, Principal principal) {

        // 投稿取得
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("投稿が見つかりません。"));

        // モデルに投稿データを追加
        model.addAttribute("post", post);
        model.addAttribute("boardContent", "post"); // 表示用フラグメント

        return "layout";
    }

    // 投稿作成画面表示
    @GetMapping("/write")
    public String writeForm(@PathVariable String boardName, Model model, Principal principal) {

        // 空のPostFormをモデルに追加
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("boardContent", "post-write"); // 投稿作成用フラグメント

        return "layout";
    }

    // 投稿編集画面表示
    @GetMapping("/update/{postId}")
    public String updateForm(@PathVariable String boardName, @PathVariable Long postId,
                             Model model, Principal principal) {

        // 編集対象投稿を取得
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // PostFormに既存データをセット
        PostForm form = new PostForm();
        form.setPostId(post.getPostId());
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());

        // モデルにフォームデータを追加
        model.addAttribute("postForm", form);
        model.addAttribute("boardContent", "post-write"); // 編集用フラグメント

        return "layout";
    }
}