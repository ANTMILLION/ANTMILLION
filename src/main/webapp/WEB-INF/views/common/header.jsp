<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/common.jsp" %>


<header class="header">
    <div class="header-search-box">
        <input type="text" id="stockSearchInput" placeholder="종목을 검색하세요" autocomplete="off">
        <button class="header-search-btn" id="stockSearchBtn">
            <img
                src="${cpath}/resources/images/icon/search.png"
                alt="검색" class="header-search-icon">
        </button>

        <!-- 검색 결과 드롭다운 -->
        <div class="search-dropdown" id="searchDropdown" style="display: none;">
            <!-- 자동완성 결과가 여기에 표시됩니다 -->
        </div>

        <!-- 에러 메시지 -->
        <div class="search-error-message" id="searchErrorMessage" style="display: none;">
            올바른 종목명을 검색해주세요
        </div>
    </div>

    <!-- ⭐ 매매 편향 경고 뱃지 -->
    <div class="bias-alert-badge" id="biasAlertBadge" style="display:none;">
        <span class="bias-alert-icon">⚠️</span>
        <span class="bias-alert-text">위험회피 주의</span>
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

        <c:choose>
            <c:when test="${not empty userRank}">
                <div class="header-user-profile-wrapper">
                    <div class="header-user-profile" id="userProfile">
                        <span class="header-user-name">${userRank.nickName}</span>
                        <span class="header-dropdown-arrow">▼</span>
                    </div>

                    <!-- 프로필 모달 -->
                    <div class="header-profile-modal" id="profileModal">
                        <div class="header-profile-modal-header">
                            <button class="header-profile-modal-close" onclick="closeProfile()">×</button>
                        </div>

                        <div class="header-profile-modal-content">
                            <img
                                src="${cpath}/${userRank.rankImage}"
                                alt="프로필" class="header-profile-avatar"
                                onerror="this.src='${cpath}/resources/images/defaultant.png'">
                            <div class="header-profile-tier">${userRank.rankName} 개미</div>
                            <div class="header-profile-nickname">${userRank.nickName}</div>
                        </div>
                        <div class="header-profile-modal-footer">
                            <form action="${cpath}/logout" method="post" onsubmit="return confirm('로그아웃 하시겠습니까?');">
                                <button type="submit" class="header-logout-btn">로그아웃</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <button type="button" class="header-login-btn"
                        onclick="location.href='${cpath}/login'">로그인
                </button>
            </c:otherwise>
        </c:choose>
    </div>
</header>

<script>
    // ========== 종목 검색 기능 ==========
    const stockSearchInput = document.getElementById('stockSearchInput');
    const stockSearchBtn = document.getElementById('stockSearchBtn');
    const searchDropdown = document.getElementById('searchDropdown');
    const searchErrorMessage = document.getElementById('searchErrorMessage');

    let debounceTimer;
    let currentSearchResults = [];

    // 검색어 입력 시 자동완성
    stockSearchInput.addEventListener('input', function(e) {
        const keyword = e.target.value.trim();

        // 에러 메시지 숨김
        searchErrorMessage.style.display = 'none';

        if (keyword.length === 0) {
            searchDropdown.style.display = 'none';
            currentSearchResults = []
            clearTimeout(debounceTimer);
            return;
        }

        // 디바운싱: 300ms 후에 API 호출
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            fetchStockAutocomplete(keyword);
        }, 300);
    });

    // 자동완성 API 호출
    function fetchStockAutocomplete(keyword) {
        fetch('${cpath}/api/stock/search/autocomplete?searchKeyword=' + encodeURIComponent(keyword))
            .then(response => response.json())
            .then(data => {
                currentSearchResults = data;
                displaySearchResults(data);
            })
            .catch(error => {
                console.error('검색 오류:', error);
                searchDropdown.style.display = 'none';
            });
    }

    // 검색 결과 표시
    function displaySearchResults(results) {
        if (results.length === 0) {
            searchDropdown.style.display = 'none';
            return;
        }

        let html = '';
        results.forEach(stock => {
            html += '<div class="search-dropdown-item" onclick="selectStock(\'' +
                stock.stockCode + '\', \'' + stock.stockName + '\')">' +
                '<div class="search-stock-name">' + stock.stockName + '</div>' +
                '<div class="search-stock-code">(' + stock.stockCode + ')</div>' +
                '</div>';
        });

        searchDropdown.innerHTML = html;
        searchDropdown.style.display = 'block';
    }

    // 종목 선택
    function selectStock(stockCode, stockName) {
        stockSearchInput.value = stockName;
        searchDropdown.style.display = 'none';
        searchErrorMessage.style.display = 'none';

        // 종목 상세 페이지로 이동
        location.href = '${cpath}/stock/detail?code=' + stockCode;
    }

    // 검색 버튼 클릭
    stockSearchBtn.addEventListener('click', function() {
        const stockName = stockSearchInput.value.trim();

        if (stockName.length === 0) {
            return;
        }

        // 종목명 검증
        fetch('${cpath}/api/stock/search/validate?stockName=' + encodeURIComponent(stockName))
            .then(response => response.json())
            .then(data => {
                if (data.exists) {
                    // 존재하는 종목이면 상세 페이지로 이동
                    location.href = '${cpath}/stock/detail?code=' + data.stockCode;
                } else {
                    // 존재하지 않는 종목이면 에러 메시지 표시
                    searchErrorMessage.style.display = 'block';
                    searchDropdown.style.display = 'none';
                }
            })
            .catch(error => {
                console.error('검증 오류:', error);
                searchErrorMessage.style.display = 'block';
            });
    });

    // Enter 키 입력 시 검색
    stockSearchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            stockSearchBtn.click();
        }
    });

    // 다른 곳 클릭 시 드롭다운 및 에러 메시지 숨김
    document.addEventListener('click', function(e) {
        const searchBox = document.querySelector('.header-search-box');
        if (searchBox && !searchBox.contains(e.target)) {
            searchDropdown.style.display = 'none';
            searchErrorMessage.style.display = 'none';
        }
    });

// 페이지 로드 시 안 읽은 알림 확인
async function checkUnreadAlerts() {
    try {
        const response = await fetch('/antmillion/api/history/unread-count?userId=1');
        const count = await response.json();
        
        const notifBtn = document.querySelector('.header-notification-btn');
        if (count > 0) {
            notifBtn.classList.add('has-unread');
            sessionStorage.setItem('hasUnreadAlert', 'true'); 
            console.log('[알림] 안 읽은 알림:', count + '개');
        } else {
            notifBtn.classList.remove('has-unread');
            sessionStorage.removeItem('hasUnreadAlert'); 
            console.log('[알림] 안 읽은 알림 없음');
        }
    } catch (error) {
        console.error('[알림] 조회 실패:', error);
    }
}

// 알림 읽음 처리
async function markNotificationAsRead(historyId) {
    try {
        await fetch('/antmillion/api/history/mark-read/' + historyId, {
            method: 'POST'
        });
        console.log('[알림] 읽음 처리 완료:', historyId);
        
        // 빨간 점 다시 체크
        checkUnreadAlerts();
    } catch (error) {
        console.error('[알림] 읽음 처리 실패:', error);
    }
}

function closeProfile() {
    document.getElementById('profileModal').classList.remove('show');
}

function showBiasAlert(biasType = 'RISK_AVERSION') {
    const badge = document.getElementById('biasAlertBadge');
    if (badge) {
        const textSpan = badge.querySelector('.bias-alert-text');
        
        // 편향 타입에 따라 텍스트 및 색상 변경
        if (biasType === 'LOSS_AVERSION') {
            textSpan.textContent = '손실회피 주의';
            badge.classList.remove('risk-aversion');  // 초록색 제거
        } else {
            textSpan.textContent = '위험회피 주의';
            badge.classList.add('risk-aversion');  // 초록색 추가
        }
        
        badge.style.display = 'flex';
        console.log(biasType + ' 경고 표시');
    }
}

function hideBiasAlert() {
    const badge = document.getElementById('biasAlertBadge');
    if (badge) {
        badge.style.display = 'none';
        console.log('경고 제거');
    }
}

document.addEventListener('DOMContentLoaded', function () {
    // 빨간 점만 복원 (알림창 열어야 꺼짐)
    const hasUnreadAlert = sessionStorage.getItem('hasUnreadAlert');
    if (hasUnreadAlert === 'true') {
        const notifBtn = document.querySelector('.header-notification-btn');
        notifBtn.classList.add('has-unread');
        console.log('[초기 로드] 세션 기반 빨간 점 복원');
    }
    
    // 경고 배지는 복원하지 않음 (페이지마다 새로 체크)
    console.log('[초기 로드] 경고 배지는 각 페이지에서 체크');

    const userProfile = document.getElementById('userProfile');
    const profileModal = document.getElementById('profileModal');

    if (userProfile) {
        userProfile.addEventListener('click', function(e) {
            e.stopPropagation();
            profileModal.classList.toggle('show');
            document.getElementById('notifBox').style.display = 'none';
        });
    }

    document.addEventListener('click', function(e) {
        const wrapper = document.querySelector('.header-user-profile-wrapper');
        const notifBtn = document.querySelector('.header-notification-btn');
        const notifBox = document.getElementById('notifBox');

        if (wrapper && !wrapper.contains(e.target)) {
            profileModal.classList.remove('show');
        }

        if (notifBtn && !notifBtn.contains(e.target) && !notifBox.contains(e.target)) {
            notifBox.style.display = 'none';
        }
    });
});

// 뒤로가기/앞으로가기 대응
window.addEventListener('pageshow', function(event) {
    console.log('[pageshow] 이벤트 발생, bfcache:', event.persisted);
    
    // 빨간 점만 복원 (경고 배지는 복원 안 함)
    const hasUnreadAlert = sessionStorage.getItem('hasUnreadAlert');
    if (hasUnreadAlert === 'true') {
        const notifBtn = document.querySelector('.header-notification-btn');
        if (notifBtn && !notifBtn.classList.contains('has-unread')) {
            notifBtn.classList.add('has-unread');
            console.log('[뒤로가기] 빨간 점 복원');
        }
    }
});


function toggleNotifications(e) {
    if (e) e.stopPropagation();

    const notifBox = document.getElementById('notifBox');
    const isVisible = notifBox.style.display === 'block';

    const profileModal = document.getElementById('profileModal');
    if (profileModal) {
        profileModal.classList.remove('show');
    }
    notifBox.style.display = isVisible ? 'none' : 'block';

    if (!isVisible) {
        hideBiasAlert();
        
        // 빨간 점 제거
        const notifBtn = document.querySelector('.header-notification-btn');
        notifBtn.classList.remove('has-unread');
        sessionStorage.removeItem('hasUnreadAlert'); 
        console.log('[알림] 알림창 열림 - 빨간 점 제거');
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
