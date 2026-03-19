package com.littlestar.task.service;

import com.littlestar.task.Exception.BusinessException;
import com.littlestar.task.Exception.ErrorCode;
import com.littlestar.task.domain.PostForm;
import com.littlestar.task.entity.*;
import com.littlestar.task.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final PostReactionRepository postReactionRepository;
    private final ImageRepository imageRepository;

    // 新しい投稿を作成
    @Override
    public void createPost(PostForm form, String boardName, String loginId) {
        // ユーザーを取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND1));

        // 掲示板を取得
        Board board = boardRepository.findByName(boardName)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // エンティティ作成およびデータマッピング
        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setUser(user);
        post.setBoard(board);

        // 保存
        postRepository.save(post);
        List<String> urls = extractImageUrls(form.getContent());

        for (String url : urls) {
            Image image = (Image) imageRepository.findByImageUrl(url).orElse(null);

            if (image != null) {
                image.setPost(post);
            }
        }
    }

    private List<String> extractImageUrls(String content) {
        List<String> urls = new ArrayList<>();

        if (content == null) return urls;

        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            urls.add(matcher.group(1));
        }

        return urls;
    }

    // 既存の投稿を更新
    @Override
    public void updatePost(PostForm form, String boardName, String loginId) {
        Post post = postRepository.findById(form.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 作成者とログインユーザーが一致するかチェック
        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN1);
        }

        // ダーティチェッキングによる更新
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
    }

    // 特定の投稿を削除
    @Override
    public void deletePost(Long postId, String loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 作成者とログインユーザーが一致するかチェック
        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN1);
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
