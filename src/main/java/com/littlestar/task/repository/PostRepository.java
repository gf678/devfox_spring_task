package com.littlestar.task.repository;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// PostデータにアクセスするためのJPAリポジトリ
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 特定の掲示板名で投稿を作成日降順でページング取得
    Page<Post> findByBoard_NameOrderByCreatedAtDesc(String boardName, Pageable pageable);

    // 特定の掲示板でタイトルに特定のキーワードを含む投稿を作成日降順でページング取得
    Page<Post> findByBoard_NameAndTitleContainingOrderByCreatedAtDesc(String boardName, String title, Pageable pageable);

    // 特定の掲示板で最新10件の投稿を取得
    List<Post> findTop10ByBoardOrderByCreatedAtDesc(Board board);

    // いいね数が特定の閾値以上の投稿上位10件を取得
    List<Post> findTop10ByLikesGreaterThanOrderByLikesDesc(int likesThreshold);

    // 特定の掲示板でいいね数が一定以上の投稿を作成日降順でページング取得
    Page<Post> findByBoard_NameAndLikesGreaterThanEqualOrderByCreatedAtDesc(String boardName, int likes, Pageable pageable);

    // 特定の掲示板でいいね数が一定以上かつタイトルにキーワードを含む投稿を作成日降順でページング取得
    Page<Post> findByBoard_NameAndLikesGreaterThanEqualAndTitleContainingOrderByCreatedAtDesc(String boardName, int likes, String keyword, Pageable pageable);
}
