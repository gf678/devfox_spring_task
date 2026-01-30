//1. グローバル状態変数
// 現在作成中のコメントが「通常コメント」か「返信(サブコメント)」かを判別するためのIDを保持
let currentParentId = null;

document.addEventListener('DOMContentLoaded', () => {
    // コメント送信ボタン要素を選択
    const submitBtn = document.querySelector('#btn-comment-submit');

    //2. コメントおよび返信の登録ロジック (POST)
    submitBtn?.addEventListener('click', function() {
        const content = document.querySelector('#comment-input').value; // 入力内容
        const postId = this.dataset.postId; // ボタンに保持された投稿ID

        // バリデーション: 空コメントを防止
        if (!content.trim()) {
            alert("コメント内容を入力してください。");
            return;
        }

        // Spring Security CSRF保護用ヘッダーおよびトークン情報取得 (HTML metaタグベース)
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;
        const token = document.querySelector('meta[name="_csrf"]')?.content;

        // サーバーへ送信するJSONデータ作成
        const requestData = {
            content: content,
            parentId: currentParentId // グローバル変数を使用して親コメントの有無を送信
        };

        // 非同期fetch API呼び出し
        fetch(`/api/posts/${postId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token // CSRFトークンをヘッダーに含める
            },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                if (response.ok) {
                    // 成功時は画面をリロードして最新コメントリストを反映
                    location.reload();
                } else {
                    alert("コメント登録に失敗しました。");
                }
            })
            .catch(err => console.error("Error:", err));
    });

    // 3. 返信ボタンクリックイベント (イベント委譲方式)
    // - 動的に追加されたコメント要素にも対応できるようにdocumentで監視
    document.addEventListener('click', function(e) {
        // クリックされた要素が 'reply-btn' クラスを持っているか確認
        if (e.target && e.target.classList.contains('reply-btn')) {
            const button = e.target;
            const commentId = button.dataset.id;     // クリックしたコメントのID
            const alias = button.dataset.alias;      // クリックしたコメントの投稿者のニックネーム

            // 1. グローバル変数に親IDを設定 (返信モードを有効化)
            currentParentId = commentId;

            // 2. UI更新: 誰に返信するかを表示
            const infoBox = document.querySelector('#reply-info');
            const targetName = document.querySelector('#reply-target-name');
            if (infoBox && targetName) {
                targetName.innerText = alias; // 対象のニックネームを挿入
                infoBox.style.display = 'block'; // 案内バーを表示
            }

            // 3. 利便性向上: 入力フィールドにスムーズスクロール＆フォーカス
            const inputField = document.querySelector('#comment-input');
            inputField?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            inputField?.focus();
        }
    });
});

// 4. 返信キャンセル関数 (グローバル関数)
// 返信モードを終了して通常コメントモードに戻す
function cancelReply() {
    // グローバル変数初期化
    currentParentId = null;

    // UI更新: 返信案内バーを非表示
    const infoBox = document.querySelector('#reply-info');
    if (infoBox) infoBox.style.display = 'none';
}

//5. 投稿のいいね/よくないねロジック
document.querySelectorAll('.btn-reaction').forEach(btn => {
    btn.addEventListener('click', function() {
        // クリックしたボタンのクラス名で「いいね」または「よくないね」を判別
        const type = this.classList.contains('like') ? 'LIKE' : 'DISLIKE';
        const postId = document.querySelector('#btn-comment-submit').dataset.postId;

        // CSRFセキュリティトークン取得
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;
        const token = document.querySelector('meta[name="_csrf"]')?.content;

        // サーバーAPI呼び出し
        fetch(`/api/posts/${postId}/reaction?type=${type}`, {
            method: 'POST',
            headers: { [header]: token }
        })
            .then(async res => {
                // 成功時は結果データ(json)を返し、失敗時はサーバーエラーメッセージを投げる
                if (res.ok) return res.json();
                const errorText = await res.text();
                throw new Error(errorText || "エラーが発生しました。");
            })
            .then(data => {
                // サーバーから返された最新のいいね/よくないね数で画面を即時更新
                document.querySelector('.like span:last-child').innerText = data.likes;
                document.querySelector('.dislike span:last-child').innerText = data.dislikes;
            })
            .catch(err => alert(err.message)); // 重複投票などエラー発生時にユーザーへ通知
    });
});
