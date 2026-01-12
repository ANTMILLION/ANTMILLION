<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- 경로 설정을 위한 변수 --%>
<c:set var="cpath" value="${pageContext.request.contextPath}" />

<style>
/* 헤더 전체 컨테이너 */
.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 15px 40px;
	background-color: #ffffff;
	border-bottom: 1px solid #f0f0f0;
	height: 70px;
	box-sizing: border-box;
	/* 핵심 수정 사항 */
	margin-left: 200px; /* sidebar.css의 width와 동일하게 설정 */
}

/* 1. 검색창 영역 */
.search-box {
	display: flex;
	align-items: center;
	background-color: #f5f7fa; /* 이미지의 연한 회색 배경 */
	border-radius: 30px;
	padding: 8px 20px;
	width: 350px;
	transition: all 0.3s ease;
}

.search-box:focus-within {
	background-color: #ffffff;
	box-shadow: 0 0 0 2px #e2e8f0;
}

.search-box input {
	border: none;
	background: transparent;
	outline: none;
	margin-left: 10px;
	width: 100%;
	font-size: 14px;
	color: #333;
}

.search-box input::placeholder {
	color: #a0aec0;
}

.search-icon-img {
	width: 18px;
	height: 18px;
	opacity: 0.5;
}

/* 2. 유저 정보 영역 */
.user-info {
	display: flex;
	align-items: center;
	gap: 25px;
}

.notification-btn {
	background: none;
	border: none;
	cursor: pointer;
	padding: 5px;
	position: relative;
	display: flex;
	align-items: center;
}

.alarm-icon-img {
	width: 24px;
	height: 24px;
}

/* 알림 레드닷 (이미지에는 없지만 필요시 사용) */
.notification-btn::after {
	content: '';
	position: absolute;
	top: 5px;
	right: 5px;
	width: 6px;
	height: 6px;
	background-color: #ff4d4f;
	border-radius: 50%;
	display: none; /* 필요할 때 block으로 변경 */
}

/* 3. 유저 프로필 드롭다운 */
.user-profile-wrapper {
	position: relative;
}

.user-profile {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 6px 12px;
	border-radius: 12px;
	cursor: pointer;
	transition: background-color 0.2s;
	background-color: #f8fafc;
	border: 1px solid #edf2f7;
}

.user-profile:hover {
	background-color: #f1f5f9;
}

.profile-img {
	width: 32px;
	height: 32px;
	border-radius: 50%;
	object-fit: cover;
	background-color: #e2e8f0;
}

.user-name {
	font-size: 14px;
	font-weight: 600;
	color: #2d3748;
}

.dropdown-arrow {
	font-size: 10px;
	color: #718096;
}

/* 4. 프로필 모달 (드롭다운 메뉴) */
.profile-modal {
	position: absolute;
	top: 50px;
	right: 0;
	width: 200px;
	background: #ffffff;
	border-radius: 15px;
	box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
	border: 1px solid #f0f0f0;
	display: none;
	z-index: 1000;
	overflow: hidden;
}

.profile-modal.show {
	display: block;
	animation: fadeInDown 0.2s ease-out;
}

@
keyframes fadeInDown {from { opacity:0;
	transform: translateY(-10px);
}

to {
	opacity: 1;
	transform: translateY(0);
}

}
.profile-modal-content {
	padding: 20px;
	text-align: center;
	border-bottom: 1px solid #f7fafc;
}

.profile-avatar-large {
	width: 60px;
	height: 60px;
	border-radius: 50%;
	margin-bottom: 10px;
}

.profile-tier {
	font-size: 11px;
	color: #4a90e2;
	font-weight: bold;
	background: #ebf4ff;
	display: inline-block;
	padding: 2px 8px;
	border-radius: 4px;
	margin-bottom: 5px;
}

.profile-nickname {
	font-weight: bold;
	font-size: 15px;
}

.profile-modal-footer {
	padding: 10px;
}

.logout-btn {
	width: 100%;
	padding: 10px;
	border: none;
	background: none;
	color: #e53e3e;
	font-size: 13px;
	font-weight: 600;
	cursor: pointer;
	border-radius: 8px;
}

.logout-btn:hover {
	background-color: #fff5f5;
}

/* 5. 알림창(Dropdown) 스타일 */
    .notif-dropdown {
        position: absolute; top: 55px; right: 0; width: 380px; 
        background: rgba(255, 255, 255, 0.98); backdrop-filter: blur(15px);
        border-radius: 20px; box-shadow: 0 15px 40px rgba(0,0,0,0.18);
        border: 1px solid #eee; z-index: 9999; display: none; animation: fadeInDown 0.3s ease;
    }
    @keyframes fadeInDown {
        from { opacity: 0; transform: translateY(-15px); }
        to { opacity: 1; transform: translateY(0); }
    }
    .notif-list-item { display: flex; padding: 20px; border-bottom: 1px solid #f5f5f5; cursor: pointer; transition: 0.2s; }
    .notif-list-item:hover { background: #f0f7ff; }
    .stock-img-box { width: 50px; height: 50px; background: #1a4da1; border-radius: 12px; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 10px; margin-right: 15px; flex-shrink: 0; }

</style>

<header class="header">
	<div class="search-box">
		<img src="${cpath}/resources/images/icon/search.png" alt="검색"
			class="search-icon-img"> <input type="text"
			placeholder="종목을 검색하세요">
	</div>

	<div class="user-info">
		<button class="notification-btn" onclick="toggleNotifications(event)">
			<img src="${cpath}/resources/images/icon/alarm.png" alt="알림"
				class="alarm-icon-img">
		</button>


		<div id="notifBox" class="notif-dropdown">
			<div
				style="padding: 20px; font-weight: bold; border-bottom: 1px solid #eee; display: flex; justify-content: space-between;">
				<span>알림 메시지</span> <span style="color: #ccc; cursor: pointer;"
					onclick="toggleNotif()">✕</span>
			</div>
			<div class="notif-list-item">
				<div class="stock-img-box">SAMSUNG</div>
				<div>
					<div style="font-size: 13px; font-weight: bold;">(005930)
						삼성전자</div>
					<div style="font-size: 13px; color: #e74c3c; font-weight: bold;">⚠️
						매몰 비용 오류 경고</div>
					<div style="font-size: 11px; color: #999; margin-top: 5px;">2026/01/12
						10:30:22</div>
				</div>
			</div>
			<div class="notif-list-item">
				<div class="stock-img-box" style="background: #444;">HYUNDAI</div>
				<div>
					<div style="font-size: 13px; font-weight: bold;">(005380) 현대차</div>
					<div style="font-size: 13px; color: #333;">새로운 분석 리포트가
						도착했습니다.</div>
					<div style="font-size: 11px; color: #999; margin-top: 5px;">2026/01/12
						09:15:00</div>
				</div>
			</div>
		</div>



		<div class="user-profile-wrapper">
			<div class="user-profile" id="userProfile">
				<img src="${cpath}/resources/images/profile/gold-ant.png" alt="P"
					class="profile-img"
					onerror="this.src='${cpath}/resources/images/default-user.png'">
				<span class="user-name">John Doe</span> <span class="dropdown-arrow">▼</span>
			</div>

			<div class="profile-modal" id="profileModal">
				<div class="profile-modal-content">
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
	document.addEventListener('DOMContentLoaded', function() {
		const userProfile = document.getElementById('userProfile');
		const profileModal = document.getElementById('profileModal');

		// 프로필 클릭 시 모달 토글
		userProfile.addEventListener('click', function(e) {
			e.stopPropagation();
			profileModal.classList.toggle('show');
		});

		// 외부 클릭 시 닫기
		document.addEventListener('click', function(e) {
			if (!userProfile.contains(e.target)
					&& !profileModal.contains(e.target)) {
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
		if(e) e.stopPropagation();
		// 알림 리스트 기능을 추가하려면 여기에 구현
		const notifBox = document.getElementById('notifBox');
        if (notifBox.style.display === 'block') {
            notifBox.style.display = 'none';
        } else {
            notifBox.style.display = 'block';
        }
		
		console.log('알림창 열기');
	}

    // 창 외부 클릭 시 닫기 기능
    window.onclick = function(event) {
        const notifBox = document.getElementById('notifBox');
        const icon = document.querySelector('.notif-icon');
        if (event.target !== icon && !notifBox.contains(event.target)) {
            notifBox.style.display = 'none';
        }
    }
</script>