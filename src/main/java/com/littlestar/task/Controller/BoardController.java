package com.littlestar.task.Controller;

import com.littlestar.task.domain.PostForm;
import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    //掲示板履歴コントローラー
    @GetMapping("/board/{boardName}/list")
    String board(@PathVariable String boardName, Model model) {

        // 1. 掲示板名で掲示板情報を取得
        Board boardInfo = boardRepository.findByName(boardName)
                .orElseThrow(() -> new RuntimeException("掲示板が見つかりません: " + boardName));

        // 2. 掲示板名で投稿一覧を取得
        List<Post> posts = postRepository.findByBoard_NameOrderByCreatedAtDesc(boardName);

        // 3. モデルにデータを格納
        model.addAttribute("boardName", boardName);
        model.addAttribute("boardId", boardInfo.getBoardId());
        model.addAttribute("posts", posts);
        model.addAttribute("pageSize", 15);

        // 説明文をDBから取得
        model.addAttribute("description", boardInfo.getDescription());
        // ボディレンダリング
        model.addAttribute("boardContent", "post-list");
        model.addAttribute("content", "contents/board/board :: board");

        return "layout";
    }

    //Read
    // 投稿コントローラー
    @GetMapping("/board/{boardName}/post/{postId}")
    public String postView(@PathVariable String boardName, @PathVariable Long postId, Model model) {

        // 1. 実際のDBから投稿を取得
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("該当する投稿が見つかりません。"));

        // 2. 掲示板情報の確認（アドレスバーのboardNameと実際の投稿の掲示板が一致するか確認用）
        Board boardInfo = boardRepository.findByName(boardName)
                .orElseThrow(() -> new RuntimeException("掲示板が見つかりません。"));

        // 3. 閲覧数増加ロジック
        post.setViews(post.getViews() + 1);
        postRepository.save(post);

        // 4. モデルに実際のデータを格納
        model.addAttribute("post", post);
        model.addAttribute("boardName", boardName);
        model.addAttribute("boardId", boardInfo.getBoardId());

        model.addAttribute("description", boardInfo.getDescription());
        // ボディレンダリング
        model.addAttribute("boardContent", "post");
        model.addAttribute("content", "contents/board/board :: board");

        return "layout";
    }

    //Read
    // 投稿作成ページコントローラー
    @GetMapping("/board/{boardName}/write")
    public String writeForm(@PathVariable String boardName, Model model) {
        Board boardInfo = boardRepository.findByName(boardName)
                .orElseThrow(() -> new RuntimeException("掲示板が見つかりません。: " + boardName));

        model.addAttribute("boardName", boardName);
        model.addAttribute("boardTitle", boardInfo.getName());

        // [수정] updateForm과 이름을 맞추기 위해 "postForm"으로 전달
        // 새 글이므로 postId는 null인 상태로 전달됩니다.
        model.addAttribute("postForm", new PostForm());

        model.addAttribute("boardContent", "post-write");
        model.addAttribute("content", "contents/board/board :: board");

        return "layout";
    }
    // 投稿作成ページコントローラー
    @GetMapping("/board/{boardName}/update/{postId}")
    public String updateForm(@PathVariable String boardName,
                             @PathVariable Long postId,
                             Model model) {

        // 1. 기존 게시글을 DB에서 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 2. [핵심] 조회한 데이터를 PostForm 가방에 수동으로 넣어줘야 함!
        PostForm form = new PostForm();
        form.setPostId(post.getPostId());
        form.setTitle(post.getTitle());     // 이게 빠지면 제목이 안 나옵니다.
        form.setContent(post.getContent()); // 이게 빠지면 내용이 안 나옵니다.

        // 3. 모델에 "postForm"이라는 이름으로 전달
        model.addAttribute("postForm", form);
        model.addAttribute("boardName", boardName);

        // 나머지 속성들 설정...
        model.addAttribute("boardContent", "post-write");
        model.addAttribute("content", "contents/board/board :: board");

        return "layout";
    }
}