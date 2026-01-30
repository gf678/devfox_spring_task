package com.littlestar.task.repository;

import com.littlestar.task.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Board（掲示板）エンティティにアクセスするためのJPAリポジトリ
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 掲示板の名前で情報を検索
    Optional<Board> findByName(String name);
}
