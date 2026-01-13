<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/common.jsp" %>
<!-- 헤더 -->
<header class="header">
	<div class="header-search-box">
		<input type="text" placeholder="종목을 검색하세요">
		<button class="header-search-btn">
			<img
				src="${cpath}/resources/images/icon/search.png"
				alt="검색" class="header-search-icon">
		</button>
	</div>

	<div class="header-user-info">
		<button class="header-notification-btn" onclick="toggleNotifications(event)">
			<img
				src="${cpath}/resources/images/icon/alarm.png" alt="알림" class="header-alarm-icon">
		</button>

		<div id="notifBox" class="header-notif-dropdown">
			<div
				style="padding: 20px; font-weight: bold; border-bottom: 1px solid #eee; display: flex; justify-content: space-between;">
				<span>알림 메시지</span> <span style="color: #ccc; cursor: pointer;"
					onclick="toggleNotif()">✕</span>
			</div>
			<div class="header-notif-list-item">
				<div class="header-stock-img-box">SAMSUNG</div>
				<div>
					<div style="font-size: 13px; font-weight: bold;">(005930)
						삼성전자</div>
					<div style="font-size: 13px; color: #e74c3c; font-weight: bold;">⚠️
						매몰 비용 오류 경고</div>
					<div style="font-size: 11px; color: #999; margin-top: 5px;">2026/01/12
						10:30:22</div>
				</div>
			</div>
			<div class="header-notif-list-item">
				<div class="header-stock-img-box" style="background: #444;">HYUNDAI</div>
				<div>
					<div style="font-size: 13px; font-weight: bold;">(005380) 현대차</div>
					<div style="font-size: 13px; color: #333;">새로운 분석 리포트가
						도착했습니다.</div>
					<div style="font-size: 11px; color: #999; margin-top: 5px;">2026/01/12
						09:15:00</div>
				</div>
			</div>
		</div>

		<div class="header-user-profile-wrapper">
			<div class="header-user-profile" id="userProfile">
				<span class="header-user-name">John Doe</span> <span class="header-dropdown-arrow">▼</span>
			</div>

			<!-- 프로필 모달 -->
			<div class="header-profile-modal" id="profileModal">
				<div class="header-profile-modal-header">
					<button class="header-profile-modal-close" onclick="closeProfile()">×</button>
				</div>

				<div class="header-profile-modal-content">
					<!-- via 제거 버전 -->
					<img
						src="${cpath}/resources/images/profile/gold-ant.png"
						alt="프로필" class="header-profile-avatar"
						onerror="this.style.display='none'">
					<div class="header-profile-tier">골드 티어</div>
					<div class="header-profile-nickname">John Doe</div>
				</div>

				<div class="header-profile-modal-footer">
					<button class="header-logout-btn" onclick="logout()">로그아웃</button>
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
	
function toggleNotifications(e) {
    // 1. 이벤트 전파 방지 (클릭하자마자 window.onclick이 실행되어 닫히는 현상 방지)
    if(e) e.stopPropagation(); 
    
    const notifBox = document.getElementById('notifBox');
    // 스타일을 직접 체크하기보다 현재 상태를 보고 토글
    const isVisible = notifBox.style.display === 'block';
    
    // 프로필 창이 열려있다면 닫아주는 센스
    document.getElementById('profileModal').classList.remove('show');
    
    notifBox.style.display = isVisible ? 'none' : 'block';
    console.log('알림창 토글');
}

// 창 외부 클릭 시 닫기 기능 수정
window.onclick = function(event) {
    const notifBox = document.getElementById('notifBox');
    // .notif-icon 대신 실제 사용 중인 .alarm-icon-img 사용
    const icon = document.querySelector('.alarm-icon-img');
    
    if (event.target !== icon && !notifBox.contains(event.target)) {
        notifBox.style.display = 'none';
    }
}

// ✕ 버튼 클릭 시 닫기 기능
function toggleNotif() {
    document.getElementById('notifBox').style.display = 'none';
}
</script>
