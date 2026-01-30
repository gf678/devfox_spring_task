package com.littlestar.task.repository;

import com.littlestar.task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//UserデータにアクセスするためのJPAリポジトリ
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ログインIDでユーザー情報を取得
    Optional<User> findByLoginId(String loginId);

    // 指定されたログインIDが存在するか確認
    boolean existsByLoginId(String loginId);

    // ログインIDに部分一致するユーザーを検索
    List<User> findByLoginIdContaining(String loginId);
}
