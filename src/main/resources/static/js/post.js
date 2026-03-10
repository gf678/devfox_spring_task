// 1. グローバル状態変数
// 現在作成中のコメントが「通常コメント」か「返信コメント」かを判別するための親コメントID
let currentParentId = null;

document.addEventListener("DOMContentLoaded", function () {

    // コメント送信ボタン
    const submitBtn = document.querySelector('#btn-comment-submit');

    // コメント登録処理
    submitBtn?.addEventListener('click', function() {

        const content = document.querySelector('#comment-input').value;
        const postId = this.dataset.postId;

        // 入力チェック（空コメント防止）
        if (!content.trim()) {
            alert("コメント内容を入力してください。");
            return;
        }

        // Spring Security の CSRF トークン取得
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;
        const token = document.querySelector('meta[name="_csrf"]')?.content;

        // サーバーへ送信するデータ
        const requestData = {
            content: content,
            parentId: currentParentId // 親コメントID（返信の場合）
        };

        // コメント登録API呼び出し
        fetch(`/api/posts/${postId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(requestData)
        })
            .then(res => {
                if (res.ok) location.reload(); // 成功時はページ更新
                else alert("コメント登録に失敗しました。");
            })
            .catch(err => console.error(err));

    });


    // 返信ボタン処理（イベント委譲）
    document.addEventListener('click', function(e) {

        if (e.target && e.target.classList.contains('reply-btn')) {

            const button = e.target;
            const commentId = button.dataset.id;
            const alias = button.dataset.alias;

            // 返信対象コメントIDを保存
            currentParentId = commentId;

            // UI更新（返信対象ユーザー表示）
            const infoBox = document.querySelector('#reply-info');
            const targetName = document.querySelector('#reply-target-name');

            if (infoBox && targetName) {
                targetName.innerText = alias;
                infoBox.style.display = 'block';
            }

            // コメント入力欄へスクロール＆フォーカス
            const inputField = document.querySelector('#comment-input');
            inputField?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            inputField?.focus();
        }

    });


    // 投稿の「いいね / よくないね」処理
    document.querySelectorAll('.btn-reaction').forEach(btn => {

        btn.addEventListener('click', function() {

            const type = this.classList.contains('like') ? 'LIKE' : 'DISLIKE';
            const postId = document.querySelector('#btn-comment-submit').dataset.postId;

            // CSRFトークン取得
            const header = document.querySelector('meta[name="_csrf_header"]')?.content;
            const token = document.querySelector('meta[name="_csrf"]')?.content;

            // リアクションAPI呼び出し
            fetch(`/api/posts/${postId}/reaction?type=${type}`, {
                method: 'POST',
                headers: { [header]: token }
            })
                .then(async res => {
                    if (res.ok) return res.json();
                    const errorText = await res.text();
                    throw new Error(errorText);
                })
                .then(data => {
                    // 画面のリアクション数を更新
                    document.querySelector('.like span:last-child').innerText = data.likes;
                    document.querySelector('.dislike span:last-child').innerText = data.dislikes;
                })
                .catch(err => alert(err.message));

        });

    });


    // コメント編集フォーム表示 / 非表示
    document.querySelectorAll(".edit-btn").forEach(btn => {

        btn.addEventListener("click", function () {

            const id = this.dataset.id;
            const form = document.getElementById("edit-form-" + id);

            form.style.display = form.style.display === "none" ? "block" : "none";

        });

    });

    document.querySelectorAll(".delete-btn").forEach(btn => {

        btn.addEventListener("click", function () {

            const id = this.dataset.id;
            const postId = document.querySelector('#btn-comment-submit').dataset.postId;

            if (!confirm("本当に削除しますか？")) return;

            const header = document.querySelector('meta[name="_csrf_header"]')?.content;
            const token = document.querySelector('meta[name="_csrf"]')?.content;

            fetch(`/api/posts/${postId}/comments/delete/${id}`, {
                method: "POST",
                headers: {
                    [header]: token
                }
            })
                .then(res => {
                    if (!res.ok) throw new Error("削除失敗");
                    return res.text();
                })
                .then(() => location.reload())
                .catch(err => console.error(err));

        });

    });


    // コメント編集内容保存
    document.querySelectorAll(".save-edit").forEach(btn => {

        btn.addEventListener("click", function () {

            const id = this.dataset.id;
            const textarea = document.querySelector("#edit-form-" + id + " textarea");
            const content = textarea.value;

            const postId = document.querySelector('#btn-comment-submit').dataset.postId;

            // CSRFトークン取得
            const header = document.querySelector('meta[name="_csrf_header"]')?.content;
            const token = document.querySelector('meta[name="_csrf"]')?.content;

            // コメント更新API呼び出し
            fetch(`/api/posts/${postId}/comments/update/${id}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [header]: token
                },
                body: JSON.stringify({
                    content: content
                })
            })
                .then(res => res.text())
                .then(() => location.reload());

        });
    });

    document.querySelectorAll(".cancel-edit").forEach(btn => {
        btn.addEventListener("click", function () {

            const id = this.dataset.id;
            const form = document.getElementById("edit-form-" + id);

            form.style.display = "none";

        });
    });

});


// 返信キャンセル処理
// 返信モードを解除して通常コメントモードへ戻す
function cancelReply() {

    currentParentId = null;

    const infoBox = document.querySelector('#reply-info');
    if (infoBox) infoBox.style.display = 'none';

}