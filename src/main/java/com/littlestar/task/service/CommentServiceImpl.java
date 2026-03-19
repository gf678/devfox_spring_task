package com.littlestar.task.service;

import com.littlestar.task.Exception.BusinessException;
import com.littlestar.task.Exception.ErrorCode;
import com.littlestar.task.entity.Comment;
import com.littlestar.task.entity.Post;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.CommentRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// コメントビジネスロジックを実装するサービスクラス
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // コメントまたは返信をデータベースに保存
    @Override
    @Transactional
    public void saveComment(Long postId, String content, String loginId, Long parentId) {

        // コメントを投稿する記事が実際に存在するかDBで確認
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // ログイン中のIDを基に作成者情報を取得
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND1));

        // コメントオブジェクトを作成し基本情報を設定
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        // 返信ロジックの処理
        // クライアントから親コメントID(parentId)が渡された場合のみ実行
        if (parentId != null) {
            // 親コメントが実際に存在するか確認
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND2));

            // 現在のコメントを親コメントの子として設定
            comment.setParent(parent);
        }

        // 作成されたコメント（または返信）をDBに保存
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void updateComment(Long id, String content) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow();

        comment.setContent(content);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND1));

        comment.setIsDeleted(true);
    }
}
