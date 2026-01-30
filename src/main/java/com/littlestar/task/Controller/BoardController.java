package com.littlestar.task.Controller;

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

import java.security.Principal;

@Controller
@RequestMapping("/board/{boardName}")
@RequiredArgsConstructor
public class BoardController {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    // ヘッダー・フッター用Viewモデル挿入クラス
    private void addBoardInfoToModel(String boardName, Principal principal, Model model) {
        // DBで掲示板の存在有無を確認
        Board board = boardRepository.findByName(boardName)
                .orElseThrow(() -> new RuntimeException("掲示板が見つかりません： " + boardName));

        // ログイン状態の場合、そのユーザーがこの掲示板を購読しているか確認
        boolean isSubscribed = false;
        if (principal != null) {
            isSubscribed = userRepository.findByLoginId(principal.getName())
                    .map(user -> subscriptionRepository.existsByUserAndBoard(user, board))
                    .orElse(false);
        }

        // ビューで共通的に使用する属性を追加
        model.addAttribute("boardName", boardName);
        model.addAttribute("boardId", board.getBoardId());
        model.addAttribute("description", board.getDescription());
        model.addAttribute("isSubscribed", isSubscribed);

        // Thymeleafレイアウト構造で本文として使用するフラグメントのパスを設定
        model.addAttribute("content", "contents/board/board :: board");
    }

    //投稿一覧
    @GetMapping("/list")
    public String boardList(@PathVariable String boardName,
                            @RequestParam(value = "page", defaultValue = "0") int page, //paging
                            @RequestParam(value = "size", defaultValue = "15") int size, // 1ページあたりの投稿数
                            @RequestParam(value = "keyword", required = false) String keyword, // 検索用キーワード
                            @RequestParam(value = "sort", required = false) String sort, // 一般投稿・人気投稿分類用
                            Model model, Principal principal) {

        // 共通掲示板情報の注入
        addBoardInfoToModel(boardName, principal, model);

        // ページング設定：最新順にソート
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> paging;

        // 人気投稿のソート
        if ("popular".equals(sort)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 人気投稿 - 検索
                paging = postRepository.findByBoard_NameAndLikesGreaterThanEqualAndTitleContainingOrderByCreatedAtDesc(
                        boardName, 10, keyword, pageable);
            } else {
                // 人気投稿 - デフォルト
                paging = postRepository.findByBoard_NameAndLikesGreaterThanEqualOrderByCreatedAtDesc(
                        boardName, 10, pageable);
            }
        }
        // 一般投稿のソート
        else if (keyword != null && !keyword.trim().isEmpty()) {
            // 一般投稿 - 検索
            paging = postRepository.findByBoard_NameAndTitleContainingOrderByCreatedAtDesc(boardName, keyword, pageable);
        } else {
            // 一般投稿 - デフォルト
            paging = postRepository.findByBoard_NameOrderByCreatedAtDesc(boardName, pageable);
        }

        // ビューで共通的に使用する属性を追加
        model.addAttribute("paging", paging);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("boardContent", "post-list");

        return "layout";
    }

    //投稿表示
    @GetMapping("/post/{postId}")
    public String postView(@PathVariable String boardName, @PathVariable Long postId,
                           Model model, Principal principal) {

        addBoardInfoToModel(boardName, principal, model);

        // 投稿の詳細情報を取得
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("投稿が見つかりません。"));

        // 閲覧数を増加
        post.setViews(post.getViews() + 1);
        postRepository.save(post);

        model.addAttribute("post", post);
        model.addAttribute("boardContent", "post"); // post.html 조각을 렌더링

        return "layout";
    }

    // 投稿作成ページ
    @GetMapping("/write")
    public String writeForm(@PathVariable String boardName, Model model, Principal principal) {
        addBoardInfoToModel(boardName, principal, model);


        model.addAttribute("postForm", new PostForm()); // 投稿保存用の空DTOオブジェクトを渡す
        model.addAttribute("boardContent", "post-write"); // ボディを投稿作成用HTMLに差し替え

        return "layout";
    }

    // 既存投稿の編集
    @GetMapping("/update/{postId}")
    public String updateForm(@PathVariable String boardName, @PathVariable Long postId,
                             Model model, Principal principal) {

        addBoardInfoToModel(boardName, principal, model);

        // 編集対象の元投稿を取得
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("投稿が見つかりません。"));

        // 未ログイン、またはログインユーザーと作成者が異なる場合は一覧にリダイレクト
        if (principal == null || !post.getUser().getLoginId().equals(principal.getName())) {
            return "redirect:/board/" + boardName + "/list";
        }

        // 既存データを修正
        PostForm form = new PostForm();
        form.setPostId(post.getPostId());
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());

        model.addAttribute("postForm", form); // 値が入力されたフォームを渡す
        model.addAttribute("boardContent", "post-write");

        return "layout";
    }
}