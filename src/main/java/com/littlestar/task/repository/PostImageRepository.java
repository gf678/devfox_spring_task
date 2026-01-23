package com.littlestar.task.repository;

import com.littlestar.task.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // 1. 特定の投稿に属するすべての画像を照会 (表示順の昇順)
    List<PostImage> findByPost_PostIdOrderBySortOrderAsc(Long postId);

    // 2. 特定の投稿のすべての画像を削除 (投稿の修正や削除時に使用)
    void deleteByPost_PostId(Long postId);
}