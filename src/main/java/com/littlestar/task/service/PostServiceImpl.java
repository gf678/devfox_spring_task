package com.littlestar.task.service;

import com.littlestar.task.domain.PostForm;
import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import com.littlestar.task.entity.PostReaction;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.PostReactionRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PostReactionRepository postReactionRepository;

    // 新しい投稿を作成
    @Override
    public void createPost(PostForm form, String boardName, String loginId) {
        // ユーザーを取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("該当するユーザーが見つかりません: " + loginId));

        // 掲示板を取得
        Board board = boardRepository.findByName(boardName)
                .orElseThrow(() -> new RuntimeException("掲示板 '" + boardName + "' が見つかりません。"));

        // エンティティ作成およびデータマッピング
        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setUser(user);
        post.setBoard(board);

        // 保存
        postRepository.save(post);
    }

    // 既存の投稿を更新
    @Override
    public void updatePost(PostForm form, String boardName, String loginId) {
        Post post = postRepository.findById(form.getPostId())
                .orElseThrow(() -> new RuntimeException("投稿が見つかりません。"));

        // 作成者とログインユーザーが一致するかチェック
        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new RuntimeException("修正権限がありません。");
        }

        // ダーティチェッキングによる更新
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
    }

    // 特定の投稿を削除
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

    // 投稿に対するリアクションを更新
    @Override
    @Transactional
    public Map<String, Integer> updateReaction(Long postId, String type, String loginId) {
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findByLoginId(loginId).orElseThrow();

        // すでにリアクションがあるか確認
        Optional<PostReaction> existingReaction = postReactionRepository.findByPostAndUser(post, user);

        if (existingReaction.isPresent()) {
            // すでにリアクションがある場合は例外
            throw new IllegalStateException("すでに「いいね」/「よくないね」を押しています。");
        }

        // 新しいリアクションを保存
        PostReaction reaction = new PostReaction();
        reaction.setPost(post);
        reaction.setUser(user);
        reaction.setType(type);
        postReactionRepository.save(reaction);

        // Postエンティティのカウントを更新
        if ("LIKE".equals(type)) {
            post.setLikes(post.getLikes() + 1);
        } else {
            post.setDislikes(post.getDislikes() + 1);
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("likes", post.getLikes());
        result.put("dislikes", post.getDislikes());
        return result;
    }
}
