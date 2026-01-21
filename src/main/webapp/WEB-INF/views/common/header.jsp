<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/common.jsp" %>


<header class="header">
    <div class="header-search-box">
        <input type="text" placeholder="종목을 검색하세요">
        <button class="header-search-btn">
            <img
                src="${cpath}/resources/images/icon/search.png"
                alt="검색" class="header-search-icon">
        </button>
    </div>

    <!-- ⭐ 매매 편향 경고 뱃지 -->
    <div class="bias-alert-badge" id="biasAlertBadge" style="display:none;">
        <span class="bias-alert-icon">⚠️</span>
        <span class="bias-alert-text">위험회피 경고</span>
    </div>

    <div class="header-user-info">
        <button class="header-notification-btn" onclick="toggleNotifications(event)">
            <img
                src="${cpath}/resources/images/icon/alarm.png" alt="알림" class="header-alarm-icon">
        </button>

        <div id="notifBox" class="header-notif-dropdown">
            <div
                style="padding: 20px; font-weight: bold; border-bottom: 1px solid #eee; display: flex; justify-content: space-between;">
                <span>알림 메시지</span>
                <span style="color: #ccc; cursor: pointer;" onclick="toggleNotif()">✕</span>
            </div>

            <div class="header-notif-list-item">
                <div class="header-stock-img-box">SAMSUNG</div>
                <div>
                    <div style="font-size: 13px; font-weight: bold;">(005930) 삼성전자</div>
                    <div style="font-size: 13px; color: #e74c3c; font-weight: bold;">
                        ⚠️ 매몰 비용 오류 경고
                    </div>
                    <div style="font-size: 11px; color: #999; margin-top: 5px;">
                        2026/01/12 10:30:22
                    </div>
                </div>
            </div>

            <div class="header-notif-list-item">
                <div class="header-stock-img-box" style="background:#444;">HYUNDAI</div>
                <div>
                    <div style="font-size: 13px; font-weight: bold;">(005380) 현대차</div>
                    <div style="font-size: 13px; color: #333;">
                        새로운 분석 리포트가 도착했습니다.
                    </div>
                    <div style="font-size: 11px; color: #999; margin-top: 5px;">
                        2026/01/12 09:15:00
                    </div>
                </div>
            </div>
        </div>

        <div class="header-user-profile-wrapper">
            <div class="header-user-profile" id="userProfile">
                <span class="header-user-name">John Doe</span>
                <span class="header-dropdown-arrow">▼</span>
            </div>

            <!-- 프로필 모달 -->
            <div class="header-profile-modal" id="profileModal">
                <div class="header-profile-modal-header">
                    <button class="header-profile-modal-close" onclick="closeProfile()">×</button>
                </div>

                <div class="header-profile-modal-content">
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

function showBiasAlert() {
    const badge = document.getElementById('biasAlertBadge');
    if (badge) {
        badge.style.display = 'flex';
        sessionStorage.setItem('biasAlert', 'true');
    }
}

function hideBiasAlert() {
    const badge = document.getElementById('biasAlertBadge');
    if (badge) {
        badge.style.display = 'none';
        sessionStorage.removeItem('biasAlert');
    }
}

function markNotificationUnread() {
    document
        .querySelector('.header-notification-btn')
        .classList.add('has-unread');
    sessionStorage.setItem('biasUnread', 'true');
}

function clearNotificationUnread() {
    document
        .querySelector('.header-notification-btn')
        .classList.remove('has-unread');
    sessionStorage.removeItem('biasUnread');
}

function onBiasWarningTriggered() {
    showBiasAlert();
    markNotificationUnread();
}

document.addEventListener('DOMContentLoaded', function () {
    if (sessionStorage.getItem('biasAlert') === 'true') {
        showBiasAlert();
    }

    if (sessionStorage.getItem('biasUnread') === 'true') {
        markNotificationUnread();
    }

    const userProfile = document.getElementById('userProfile');
    const profileModal = document.getElementById('profileModal');
    const notifBox = document.getElementById('notifBox');

    if (userProfile) {
        userProfile.addEventListener('click', function(e) {
            e.stopPropagation();
            profileModal.classList.toggle('show');
            notifBox.style.display = 'none';
        });
    }

    document.addEventListener('click', function(e) {
        const wrapper = document.querySelector('.header-user-profile-wrapper');
        const notifBtn = document.querySelector('.header-notification-btn');

        if (wrapper && !wrapper.contains(e.target)) {
            profileModal.classList.remove('show');
        }

        if (notifBtn && !notifBtn.contains(e.target) && !notifBox.contains(e.target)) {
            notifBox.style.display = 'none';
        }
    });
});

function toggleNotifications(e) {
    if (e) e.stopPropagation();

    const notifBox = document.getElementById('notifBox');
    const isVisible = notifBox.style.display === 'block';

    document.getElementById('profileModal').classList.remove('show');
    notifBox.style.display = isVisible ? 'none' : 'block';

    if (!isVisible) {
        hideBiasAlert();
        clearNotificationUnread();
    }
}

function toggleNotif() {
    document.getElementById('notifBox').style.display = 'none';
}

function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        location.href = '${cpath}/logout.jsp';
    }
}
</script>
