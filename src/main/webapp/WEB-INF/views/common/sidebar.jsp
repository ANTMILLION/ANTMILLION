<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/common.jsp" %>

<aside class="sidebar-sidebar">
    <div class="sidebar-logo" style="cursor: pointer">
        <div class="sidebar-logo-icon">
            <img src="${cpath}/resources/images/icontmp.png">
        </div>
        <span>ANTMILLION</span>
    </div>
    <nav>
        <a href="${cpath}/" class="sidebar-menu-item" data-path="/">
            <span><img src="${cpath}/resources/images/icon/main.png" class="sidebar-menu-icon" alt="메인"></span>
            <span>메인</span>
        </a>
        <a href="${cpath}/stocklist" class="sidebar-menu-item" data-path="/stock">
            <span><img src="${cpath}/resources/images/icon/assert.png" class="sidebar-menu-icon" alt="종목"></span>
            <span>종목</span>
        </a>
        <a href="${cpath}/mypage" class="sidebar-menu-item" data-path="/mypage">
            <span><img src="${cpath}/resources/images/icon/my.png" class="sidebar-menu-icon" alt="마이"></span>
            <span>마이페이지</span>
        </a>
        <a href="${cpath}/mission" class="sidebar-menu-item active" data-path="/mission">
            <span><img src="${cpath}/resources/images/icon/mission.png" class="sidebar-menu-icon" alt="미션"></span>
            <span>미션</span>
        </a>
    </nav>
    <div class="sidebar-menu-bottom">
        <form action="${cpath}/logout" method="post" onsubmit="return confirm('로그아웃 하시겠습니까?');">
            <button type="submit" class="sidebar-menu-item btn-link">
                <span><img src="${cpath}/resources/images/icon/logout.png" alt="로그아웃"></span>
                <span>로그아웃</span>
            </button>
        </form>
    </div>
</aside>
<script>
    // 현재 페이지에 따라 active 클래스 추가
    document.addEventListener('DOMContentLoaded', () => {
        const cpath = '${cpath}';
        const currentPath = window.location.pathname;
        const menuItems = document.querySelectorAll('.sidebar-menu-item[data-path]');

        menuItems.forEach(item => {
            // 초기화
            item.classList.remove('active');

            // data-path + cpath = 실제 URL 경로
            const targetPath = item.dataset.path;
            const fullPath = cpath + targetPath;

            // 메인인 경우
            if (targetPath === '/') {
                if (currentPath === fullPath || currentPath === cpath) {
                    item.classList.add('active');
                }
            }
            // 그 외에는 현재 경로가 해당 메뉴 경로로 시작하면 활성화
            else if (currentPath.startsWith(fullPath)) {
                item.classList.add('active');
            }
        });
    });

    const logo = document.querySelector('.sidebar-logo');
    if (logo) {
        logo.addEventListener('click', function () {
            window.location.href = '${cpath}/';
        });
    }
</script>
