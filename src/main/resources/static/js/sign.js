const signUpButton = document.getElementById('signUp');        // 右側オーバーレイの「会員登録」ボタン
const signInButton = document.getElementById('signIn');        // 左側オーバーレイの「ログイン」ボタン
const findPasswordButton = document.getElementById('findPassword'); // 「パスワードをお忘れですか？」リンク
const container = document.getElementById('container');         // 全体を囲むメインコンテナ

// 各フォームを格納するコンテナボックス
const signUpForm = document.querySelector('.sign-up-container');
const signInForm = document.querySelector('.sign-in-container');
const loginError = document.getElementById('loginError');
const findPasswordForm = document.querySelector('.find-password-container');

// 2. 初期画面設定
findPasswordForm.style.display = 'none';

// 3. 画面切替リスナー (UIインタラクション)

// Sign Upボタンクリック
signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
    container.classList.remove("find-pw-active"); // 비번찾기 모드 해제
});

// Sign Inボタンクリック
signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active"); // 왼쪽으로 다시 슬라이드
    container.classList.remove("find-pw-active");

    signUpForm.style.display = 'block';
    signInForm.style.display = 'block';
    findPasswordForm.style.display = 'none';
});

// Forgot Passwordクリック
findPasswordButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
    container.classList.add("find-pw-active"); // CSS에서 find-password-container를 보여주기 위한 클래스

    signUpForm.style.display = 'none';
    signInForm.style.display = 'none';
    findPasswordForm.style.display = 'block';
});

// 4. AJAXを使用したfetch処理 (非同期)
// 会員登録サブミット: ページリロードなしでサーバーにデータ送信
const realSignUpForm = document.querySelector('.sign-up-container form');

realSignUpForm.addEventListener('submit', (e) => {
    // ブラウザのデフォルト動作(Form Submit時のページ遷移)を停止
    e.preventDefault();

    // サーバーにデータ送信 (URLSearchParamsを使用して通常のフォームデータ形式に変換)
    fetch('/api/signUp', {
        method: 'POST',
        body: new URLSearchParams(new FormData(realSignUpForm))
    })
        .then(response => {
            if (response.ok) {
                // [UX改善] 登録成功時の後処理
                alert("会員登録が完了しました！");

                // 1. 既存の「ログインボタンクリック」ロジックを強制実行し、ログイン画面へスライド
                signInButton.click();

                // 2. 入力していた登録フォームのデータをリセット
                realSignUpForm.reset();
            } else {
                // サーバー応答がエラーの場合、サーバーからのメッセージをアラートで表示
                response.text().then(msg => alert("登録失敗: " + msg));
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("ネットワークエラーが発生しました。");
        });
});
const resetForm = document.querySelector('.find-password-container form');

if (resetForm) {

    resetForm.addEventListener('submit', function(e) {

        e.preventDefault();

        const formData = new FormData(resetForm);

        fetch('/password-reset', {
            method: 'POST',
            body: new URLSearchParams(formData)
        })
            .then(response => response.text())
            .then(msg => {

                alert(msg);

                resetForm.reset();

                container.classList.remove("find-pw-active");
                container.classList.remove("right-panel-active");

                signUpForm.style.display = 'block';
                signInForm.style.display = 'block';
                findPasswordForm.style.display = 'none';

            })
            .catch(err => {
                console.error(err);
                alert("エラーが発生しました");
            });

    });

}

// 로그인 form
const realSignInForm = document.querySelector('.sign-in-container form');

// 에러 표시 영역 (이미 위에서 선언됨)
if (realSignInForm) {

    realSignInForm.addEventListener('submit', (e) => {
        e.preventDefault();

        // 기존 에러 메시지 초기화
        loginError.style.display = 'none';
        loginError.innerText = '';

        fetch('/api/signIn', {
            method: 'POST',
            body: new URLSearchParams(new FormData(realSignInForm))
        })
            .then(async response => {

                if (response.ok) {
                    window.location.href = "/";
                } else {
                    const msg = await response.text();
                    loginError.innerText = msg;
                    loginError.style.display = 'block';
                }
            });

    });

}
