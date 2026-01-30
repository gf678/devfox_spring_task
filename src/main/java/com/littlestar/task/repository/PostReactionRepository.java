package com.littlestar.task.repository;

import com.littlestar.task.entity.Post;
import com.littlestar.task.entity.PostReaction;
import com.littlestar.task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// PostReactionデータにアクセスするためのリポジトリ
@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    // 特定の投稿と特定のユーザーに対応するリアクション情報を取得
    // ユーザーがこの投稿にすでにリアクションを残したかどうかを確認する際に使用
    Optional<PostReaction> findByPostAndUser(Post post, User user);

}
