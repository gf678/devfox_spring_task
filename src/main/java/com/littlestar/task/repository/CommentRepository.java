package com.littlestar.task.repository;

import com.littlestar.task.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Commentエンティティにアクセスするためのリポジトリ
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
