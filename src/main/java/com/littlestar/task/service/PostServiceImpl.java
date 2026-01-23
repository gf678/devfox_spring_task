package com.littlestar.task.service;

import com.littlestar.task.domain.PostForm;
import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    /** 新規投稿を作成し、DBに保存します。*/
    @Override
    public void createPost(PostForm form, String boardName, String loginId) {
        // 1. ユーザーの照会 (テスト用、修正予定)
        User user = userRepository.findByLoginId(loginId)
                .orElseGet(() -> {
                    return userRepository.save(User.builder()
                            .loginId(loginId)
                            .password("1234")
                            .nickname("匿名ユーザー")
                            .email(loginId + "@test.com")
                            .build());
                });

        // 2. 掲示板の照会 (掲示板名が一致するか確認)
        Board board = boardRepository.findByName(boardName)
                .orElseThrow(() -> new RuntimeException("掲示板 '" + boardName + "' が見つかりません。"));

        // 3. エンティティの生成および保存
        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setUser(user);
        post.setBoard(board);

        postRepository.save(post);
    }

    /**既存の投稿内容を修正します。(作成者本人確認を含む)*/
    @Override
    public void updatePost(PostForm form, String boardName, String loginId) {
        Post post = postRepository.findById(form.getPostId())
                .orElseThrow(() -> new RuntimeException("投稿が見つかりません。"));

        // 作成者とログインユーザーが一致するかチェック
        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new RuntimeException("修正権限がありません。");
        }

        // ダーティチェッキング(Dirty Checking)による更新
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
    }

    /** 指定された投稿を削除します。(作成者本人確認を含む) */
    @Override
    public void deletePost(Long postId, String loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("削除対象の投稿が見つかりません。"));

        // 作成者とログインユーザーが一致するかチェック
        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new RuntimeException("削除権限がありません。");
        }
        postRepository.delete(post);
    }
}