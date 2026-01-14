<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/common.jsp" %>

<aside class="sidebar-sidebar">
    <div class="sidebar-logo">
        <div class="sidebar-logo-icon">
            <img src="${cpath}/resources/images/defaultant.png">
        </div>
        <span>똑똑한개미</span>
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
        <a href="${cpath}/history" class="sidebar-menu-item">
            <span><img src="${cpath}/resources/images/icon/history.png" alt="히스토리"></span>
            <span>히스토리</span>
        </a>
        <a href="${cpath}/logout" class="sidebar-menu-item" onclick="logout()">
            <span><img src="${cpath}/resources/images/icon/logout.png" alt="로그아웃"></span>
            <span>로그아웃</span>
        </a>
    </div>
</aside>
<script>
    function logout() {
        if (e) e.preventDefault();
        if (confirm('로그아웃 하시겠습니까?')) {
            location.href = '${cpath}/logout'; //추후 수정 필요
        }
    }

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
</script>
