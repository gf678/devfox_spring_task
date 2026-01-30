/**
 * 1. 共通ドロップダウン切替関数
 * クリックしたメニューを開閉し、同時に他の開いているすべてのメニューを閉じる
 */
function toggleMenu(menuId, event) {
    // イベントが存在する場合、上位要素へのクリックイベント伝播(Bubbling)を停止
    // これをしないと、window.onclickが即座に実行され、メニューが開いた瞬間に閉じる
    if (event) {
        event.stopPropagation();
    }

    const targetMenu = document.getElementById(menuId);
    if (!targetMenu) return; // 対応するIDの要素がなければ関数終了

    // 現在メニューが既に開いているか確認
    const isShowing = targetMenu.classList.contains('show');

    // [相互排他] ユーザー体験向上のため、現在クリックしたメニュー以外のすべての開いているドロップダウンを一括で閉じる
    closeAllMenus();

    // 以前閉じていた場合は、該当メニューのみ再度開く
    if (!isShowing) {
        targetMenu.classList.add('show');
    }
}

/**
 * 2. すべてのメニューを閉じるユーティリティ
 * 画面内のすべてのドロップダウンインターフェースから 'show' クラスを削除して非表示にする
 */
function closeAllMenus() {
    // ドロップダウン、プロフィール、検索結果などすべてのメニュークラスを選択
    const allMenus = document.querySelectorAll('.dropdown-menu, .profile-menu, .search-dropdown');
    allMenus.forEach(menu => {
        menu.classList.remove('show');
    });
}

/**
 * 3. 検索および自動補完ロジック
 * ドキュメント読み込み完了後(DOM生成後)、検索入力に応じてリアルタイムフィルタリングを実行
 */
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('channel-search-input'); // 検索入力欄
    const searchResults = document.getElementById('search-results');   // 結果表示ドロップダウン

    // 両方の要素が存在する場合のみロジック実行
    if (searchInput && searchResults) {

        /**
         * 入力イベントリスナー: 文字が入力されるたびにリアルタイムで実行
         */
        searchInput.addEventListener('input', (e) => {
            // 入力値の前後の空白を削除し、英文字は小文字に統一(大文字小文字区別なし)
            const query = e.target.value.trim().toLowerCase();

            // 1. 入力欄が空の場合、結果表示を即時閉じて処理終了
            if (query.length === 0) {
                searchResults.classList.remove('show');
                return;
            }

            // 2. 全チャンネルドロップダウン内の項目を基に検索を実行
            const channels = Array.from(document.querySelectorAll('#channel-dropdown .channel-item'));
            let html = ''; // 検索結果HTMLを格納
            let count = 0; // マッチした項目のカウント

            // 3. フィルタリング実行
            channels.forEach(channel => {
                const name = channel.querySelector('span').innerText; // チャンネル名テキスト取得

                // 入力した検索語がチャンネル名に含まれているか確認
                if (name.toLowerCase().includes(query)) {
                    const href = channel.getAttribute('href'); // リンク取得
                    // 検索結果アイテム生成
                    html += `<a href="${href}" class="search-item">${name}</a>`;
                    count++;
                }
            });

            // 4. 画面レンダリング
            if (count > 0) {
                searchResults.innerHTML = html; // 検索結果リスト挿入
                searchResults.classList.add('show');
            } else {
                // 一致する項目がない場合は案内文表示
                searchResults.innerHTML = '<div class="no-result">一致するチャンネルはありません。</div>';
                searchResults.classList.add('show');
            }
        });

        // 検索欄クリック時の処理:
        // 検索欄内部クリック時にwindowまでイベントが到達しないようにして、検索中にウィンドウが閉じるのを防ぐ
        searchInput.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }
});

// メニュー外(空白部分)クリック時にすべてのメニューが自然に閉じる設定
window.addEventListener('click', () => {
    // どの要素をクリックしても、伝播を止めなかったクリックは最終的にここに到達
    closeAllMenus();
});
