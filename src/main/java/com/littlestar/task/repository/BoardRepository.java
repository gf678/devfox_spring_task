package com.littlestar.task.repository;

import com.littlestar.task.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 1. 掲示板の名前で照会 (例: "自由掲示板")
    Optional<Board> findByName(String name);

    // 2. 掲示板コードがある場合、コードで照会 (例: "FREE", "NOTICE")
    // Optional<Board> findByBoardCode(String boardCode);

    // 3. 有効な掲示板のみ照会する場合 (isAvailableのようなフィールドがある場合)
    // List<Board> findAllByIsAvailableTrue();
}