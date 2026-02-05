/**
 * 1. 共通ドロップダウン切替関数
 */
function toggleMenu(menuId, event) {
    if (event) event.stopPropagation();

    const targetMenu = document.getElementById(menuId);
    if (!targetMenu) return;

    const isShowing = targetMenu.classList.contains('show');

    closeAllMenus();

    if (!isShowing) {
        targetMenu.classList.add('show');
    }
}

/**
 * 2. すべてのメニューを閉じるユーティリティ
 */
function closeAllMenus() {
    const allMenus = document.querySelectorAll('.dropdown-menu, .profile-menu, .search-dropdown, .search-results');
    allMenus.forEach(menu => {
        menu.classList.remove('show');
    });
}

/**
 * 3. 検索および自動補完ロジック
 */
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('channel-search-input');
    const searchResults = document.getElementById('search-results');

    if (searchInput && searchResults) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim().toLowerCase();

            if (query.length === 0) {
                searchResults.classList.remove('show');
                return;
            }

            const channels = Array.from(document.querySelectorAll('#channel-dropdown .channel-item'));
            let html = '';
            let count = 0;

            channels.forEach(channel => {
                const nameTag = channel.querySelector('span');
                const name = nameTag ? nameTag.innerText : channel.innerText;

                if (name.toLowerCase().includes(query)) {
                    const href = channel.getAttribute('href');
                    html += `<a href="${href}" class="search-item">${name}</a>`;
                    count++;
                }
            });

            if (count > 0) {
                searchResults.innerHTML = html;
                searchResults.classList.add('show');
            } else {
                searchResults.innerHTML = '<div class="search-item" style="color: #94a3b8; cursor: default;">一致する項目なし</div>';
                searchResults.classList.add('show');
            }
        });

        searchInput.addEventListener('click', (e) => {
            e.stopPropagation();
            if(searchInput.value.trim().length > 0) {
                searchResults.classList.add('show');
            }
        });
    }
});

window.addEventListener('click', () => {
    closeAllMenus();
});