<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/common.jsp" %>
<!-- 헤더 -->
<header class="header">
    <div class="search-box">
        <input type="text" placeholder="종목을 검색하세요">
        <button class="search-btn">
            <img src="${cpath}/resources/images/icon/search.png" alt="검색" class="search-icon">
        </button>
    </div>
    
    <div class="user-info">
        <button class="notification-btn">
            <img src="${cpath}/resources/images/icon/alarm.png" alt="알림" class="alarm-icon">
        </button>
        <div class="user-profile-wrapper">
            <div class="user-profile" id="userProfile">
                <span class="user-name">John Doe</span>
                <span class="dropdown-arrow">▼</span>
            </div>
            
            <!-- 프로필 모달 -->
            <div class="profile-modal" id="profileModal">
                <div class="profile-modal-header">
                    <button class="profile-modal-close" onclick="closeProfile()">×</button>
                </div>
                
                <div class="profile-modal-content">
                    <!-- via 제거 버전 -->
<img src="${cpath}/resources/images/profile/gold-ant.png" alt="프로필" class="profile-avatar" onerror="this.style.display='none'">
                    <div class="profile-tier">골드 티어</div>
                    <div class="profile-nickname">John Doe</div>
                </div>
                
                <div class="profile-modal-footer">
                    <button class="logout-btn" onclick="logout()">로그아웃</button>
                </div>
            </div>
        </div>
    </div>
</header>

<script>
function closeProfile() {
    document.getElementById('profileModal').classList.remove('show');
}

document.addEventListener('DOMContentLoaded', function() {
    const userProfile = document.getElementById('userProfile');
    const profileModal = document.getElementById('profileModal');
    
    if (userProfile) {
        userProfile.addEventListener('click', function(e) {
            e.stopPropagation();
            profileModal.classList.toggle('show');
        });
    }
    
    // 외부 클릭시 닫기
    document.addEventListener('click', function(e) {
        const wrapper = document.querySelector('.user-profile-wrapper');
        if (!wrapper.contains(e.target)) {
            profileModal.classList.remove('show');
        }
    });
});

function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        location.href = '${cpath}/logout.jsp';
    }
}
</script>
