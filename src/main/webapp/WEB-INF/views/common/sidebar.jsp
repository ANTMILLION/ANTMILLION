<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/common.jsp" %>

<aside class="sidebar">
    <div class="logo">
        <div class="logo-icon">
            <img src="${cpath}/resources/images/defaultant.png">
        </div>
        <span>똑똑한개미</span>
    </div>
    <nav>
        <div class="menu-item" onclick="location.href='${cpath}/'">
            <span><img src="${cpath}/resources/images/icon/main.png" class="menu-icon" alt="메인"></span>
            <span>메인</span>
        </div>
        <div class="menu-item" onclick="location.href='${cpath}/detail'">
            <span><img src="${cpath}/resources/images/icon/assert.png" class="menu-icon" alt="종목"></span>
            <span>종목</span>
        </div>
        <div class="menu-item" onclick="location.href='${cpath}/mypage'">
            <span><img src="${cpath}/resources/images/icon/my.png" class="menu-icon" alt="마이"></span>
            <span>마이페이지</span>
        </div>
        <div class="menu-item active" onclick="location.href='${cpath}/mission'">
            <span><img src="${cpath}/resources/images/icon/mission.png" class="menu-icon" alt="미션"></span>
            <span>미션</span>
        </div>
    </nav>
    <div class="menu-bottom">
        <div class="menu-item" onclick="location.href='${cpath}/history'">
            <span><img src="${cpath}/resources/images/icon/history.png" alt="히스토리"></span>
            <span>히스토리</span>
        </div>
        <div class="menu-item" onclick="logout()">
            <span><img src="${cpath}/resources/images/icon/logout.png" alt="로그아웃"></span>
            <span>로그아웃</span>
        </div>
    </div>
</aside>
<script>
    function logout() {
        if (confirm('로그아웃 하시겠습니까?')) {
            location.href = 'logout.jsp';
        }
    }
    // 현재 페이지에 따라 active 클래스 추가
    document.addEventListener('DOMContentLoaded', function () {
        const currentPage = window.location.pathname.split('/').pop();
        const menuItems = document.querySelectorAll('.sidebar .menu-item');

        menuItems.forEach(item => {
            item.classList.remove('active');
            const itemText = item.querySelector('span:last-child').textContent.toLowerCase();

            if (currentPage.includes(itemText) ||
                (currentPage === 'mission.jsp' && itemText === '미션')) {
                item.classList.add('active');
            }
        });
    });
</script>