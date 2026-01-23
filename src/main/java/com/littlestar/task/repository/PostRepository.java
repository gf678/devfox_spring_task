package com.littlestar.task.repository;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


    // 特定の掲示板の投稿リストを照会します。(作成日の降順)
    List<Post> findByBoard_NameOrderByCreatedAtDesc(String boardName);

    // 特定の掲示板の最新投稿を10件取得します。(ホーム画面用)
    List<Post> findTop10ByBoardOrderByCreatedAtDesc(Board board);

    // タイトルのキーワード検索を行います。
    List<Post> findByTitleContaining(String keyword);

    // 特定のユーザーが作成した投稿リストを照会します。
    List<Post> findByUser_Id(Long userId);

    // タイトルに基づいて投稿を削除します。
    void deleteByTitle(String title);
}