package com.littlestar.task.Controller;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Subscription;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.SubscriptionRepository;
import com.littlestar.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Optional;

// サブスクリプション機能を処理するRESTコントローラー
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionRepository subscriptionRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 特定掲示板のサブスクリプション状態をトグル
    @PostMapping("/{boardId}")
    @Transactional // トランザクション適用: 作業中に例外が発生した場合はDBロールバック、変更検知を有効化
    public ResponseEntity<String> toggleSubscription(@PathVariable Long boardId, Principal principal) {

        // ログインしていないユーザーはアクセス不可
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // ログイン中のユーザーエンティティ取得
        User user = userRepository.findByLoginId(principal.getName())
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません。"));

        // 対象掲示板エンティティ取得
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("掲示板が見つかりません。"));

        // すでにサブスクリプション中か確認
        Optional<Subscription> existing = subscriptionRepository.findByUserAndBoard(user, board);

        if (existing.isPresent()) {
            // サブスクリプション解除: 既存エンティティを削除
            subscriptionRepository.delete(existing.get());

            // 即時反映: 書き込み遅延保存のクエリをDBにフラッシュ
            subscriptionRepository.flush();
            return ResponseEntity.ok("unsubscribed");

        } else {
            // サブスクリプション登録: 新しいSubscriptionエンティティ作成・保存
            Subscription sub = new Subscription();
            sub.setUser(user);
            sub.setBoard(board);
            subscriptionRepository.save(sub);

            return ResponseEntity.ok("subscribed");
        }
    }
}
