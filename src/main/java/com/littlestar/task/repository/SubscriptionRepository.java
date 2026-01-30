package com.littlestar.task.repository;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Subscription;
import com.littlestar.task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// SubscriptionデータにアクセスするためのJPAリポジトリ
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // 特定のユーザーが特定の掲示板を購読しているか確認
    Optional<Subscription> findByUserAndBoard(User user, Board board);

    // 特定のユーザーが購読しているすべての購読情報を取得
    List<Subscription> findAllByUser(User user);

    // 特定のユーザーが特定の掲示板を購読しているかどうかを確認
    boolean existsByUserAndBoard(User user, Board board);
}
