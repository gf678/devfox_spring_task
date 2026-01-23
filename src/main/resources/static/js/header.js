window.addEventListener('load', () => {
    const profile = document.getElementById('profile-pic');
    if (!profile) return;

    const menu = profile.querySelector('.profile-menu');
    if (!menu) return;

    menu.style.display = 'none'; // 메뉴 숨김 초기화

    // JWT 존재 여부로 로그인 상태 판단
    const token = localStorage.getItem("jwt");
    const isAuthenticated = !!token; // 토큰 있으면 true, 없으면 false
    console.log("JWT 존재 여부:", isAuthenticated, "token:", token);

    profile.addEventListener('click', e => {
        e.stopPropagation();

        if (!isAuthenticated) {   // 로그인 안 되어 있으면
            console.log("비로그인, 로그인 페이지로 이동");
            window.location.href = '/login';  // 로그인 페이지로 이동
            return;
        }

        // 로그인 되어 있으면 드롭다운 토글
        if (menu.style.display === 'block') {
            menu.style.display = 'none';
            console.log("메뉴 닫힘");
        } else {
            menu.style.display = 'block';
            console.log("메뉴 열림");
        }
    });

    document.addEventListener('click', () => {
        menu.style.display = 'none';
        console.log("바깥 클릭, 메뉴 닫힘");
    });
});