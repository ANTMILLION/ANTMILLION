/* ===========================
   공통 JavaScript (common.js)
   =========================== */

// DOM이 로드되면 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeCommon();
});

function initializeCommon() {
    // 프로필 드롭다운 이벤트
    initializeProfileDropdown();
    
    // 검색 기능 초기화
    initializeSearch();
}

// 프로필 드롭다운 초기화
function initializeProfileDropdown() {
    const profileElement = document.querySelector('.user-profile');
    
    if (profileElement) {
        profileElement.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleProfileDropdown();
        });
        
        // 외부 클릭 시 드롭다운 닫기
        document.addEventListener('click', function() {
            closeProfileDropdown();
        });
    }
}

function toggleProfileDropdown() {
    // 드롭다운 메뉴가 있다면 토글
    const dropdown = document.querySelector('.profile-dropdown');
    if (dropdown) {
        dropdown.classList.toggle('show');
    } else {
        // 드롭다운 메뉴 생성
        createProfileDropdown();
    }
}

function createProfileDropdown() {
    const profileElement = document.querySelector('.user-profile');
    
    const dropdown = document.createElement('div');
    dropdown.className = 'profile-dropdown';
    dropdown.innerHTML = `
        <div class="dropdown-item">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path d="M8 8C10.21 8 12 6.21 12 4C12 1.79 10.21 0 8 0C5.79 0 4 1.79 4 4C4 6.21 5.79 8 8 8Z" fill="currentColor"/>
                <path d="M8 10C4.69 10 2 12.69 2 16H14C14 12.69 11.31 10 8 10Z" fill="currentColor"/>
            </svg>
            <span>프로필</span>
        </div>
        <div class="dropdown-item">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path d="M13.5 8C13.5 8.8 12.8 9.5 12 9.5C11.2 9.5 10.5 8.8 10.5 8C10.5 7.2 11.2 6.5 12 6.5C12.8 6.5 13.5 7.2 13.5 8Z" fill="currentColor"/>
                <path d="M8 12C9.66 12 11 10.66 11 9V7C11 5.34 9.66 4 8 4C6.34 4 5 5.34 5 7V9C5 10.66 6.34 12 8 12Z" fill="currentColor"/>
                <path d="M1 8C1 4.13 4.13 1 8 1C11.87 1 15 4.13 15 8C15 11.87 11.87 15 8 15C4.13 15 1 11.87 1 8Z" stroke="currentColor" stroke-width="1.5" fill="none"/>
            </svg>
            <span>설정</span>
        </div>
        <div class="dropdown-divider"></div>
        <div class="dropdown-item logout">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path d="M11 12L15 8L11 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                <path d="M15 8H6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                <path d="M6 2H3C2.44772 2 2 2.44772 2 3V13C2 13.5523 2.44772 14 3 14H6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
            <span>로그아웃</span>
        </div>
    `;
    
    // CSS 동적 추가
    if (!document.querySelector('#profile-dropdown-styles')) {
        const style = document.createElement('style');
        style.id = 'profile-dropdown-styles';
        style.textContent = `
            .profile-dropdown {
                position: absolute;
                top: calc(100% + 8px);
                right: 0;
                background: white;
                border-radius: 12px;
                box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
                border: 1px solid var(--border-color);
                min-width: 200px;
                opacity: 0;
                transform: translateY(-10px);
                pointer-events: none;
                transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
                z-index: 1000;
            }
            
            .profile-dropdown.show {
                opacity: 1;
                transform: translateY(0);
                pointer-events: auto;
            }
            
            .dropdown-item {
                display: flex;
                align-items: center;
                gap: 12px;
                padding: 12px 16px;
                color: var(--text-secondary);
                cursor: pointer;
                transition: all 0.2s;
            }
            
            .dropdown-item:first-child {
                border-radius: 12px 12px 0 0;
            }
            
            .dropdown-item:last-child {
                border-radius: 0 0 12px 12px;
            }
            
            .dropdown-item:hover {
                background-color: var(--primary-bg);
                color: var(--text-primary);
            }
            
            .dropdown-item.logout:hover {
                background-color: rgba(239, 68, 68, 0.1);
                color: #EF4444;
            }
            
            .dropdown-divider {
                height: 1px;
                background-color: var(--border-color);
                margin: 4px 0;
            }
            
            .user-profile {
                position: relative;
            }
        `;
        document.head.appendChild(style);
    }
    
    profileElement.appendChild(dropdown);
    
    // 애니메이션을 위한 지연
    setTimeout(() => {
        dropdown.classList.add('show');
    }, 10);
}

function closeProfileDropdown() {
    const dropdown = document.querySelector('.profile-dropdown');
    if (dropdown) {
        dropdown.classList.remove('show');
        setTimeout(() => {
            dropdown.remove();
        }, 200);
    }
}

// 검색 기능 초기화
function initializeSearch() {
    const searchInput = document.querySelector('.search-input');
    
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch(this.value);
            }
        });
        
        // 검색 아이콘 클릭 이벤트
        const searchIcon = document.querySelector('.search-icon');
        if (searchIcon) {
            searchIcon.style.cursor = 'pointer';
            searchIcon.addEventListener('click', function() {
                performSearch(searchInput.value);
            });
        }
    }
}

function performSearch(query) {
    if (!query.trim()) {
        return;
    }
    
    console.log('검색어:', query);
    // 실제 검색 로직 구현
    // 예: window.location.href = 'search.jsp?q=' + encodeURIComponent(query);
    
    // 임시 알림
    showNotification('검색 중...', 'info');
}

// 토스트 알림 표시
function showNotification(message, type = 'info') {
    // 기존 알림이 있으면 제거
    const existingToast = document.querySelector('.toast-notification');
    if (existingToast) {
        existingToast.remove();
    }
    
    const toast = document.createElement('div');
    toast.className = `toast-notification toast-${type}`;
    toast.textContent = message;
    
    // CSS 동적 추가
    if (!document.querySelector('#toast-styles')) {
        const style = document.createElement('style');
        style.id = 'toast-styles';
        style.textContent = `
            .toast-notification {
                position: fixed;
                top: 24px;
                right: 24px;
                padding: 16px 24px;
                background: white;
                border-radius: 12px;
                box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
                border: 1px solid var(--border-color);
                font-size: 14px;
                font-weight: 500;
                z-index: 10000;
                animation: slideInRight 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            }
            
            .toast-notification.toast-success {
                border-left: 4px solid var(--color-positive);
                color: var(--color-positive);
            }
            
            .toast-notification.toast-error {
                border-left: 4px solid var(--color-negative);
                color: var(--color-negative);
            }
            
            .toast-notification.toast-info {
                border-left: 4px solid var(--accent-blue-dark);
                color: var(--accent-blue-dark);
            }
            
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            
            @keyframes slideOutRight {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(100%);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(toast);
    
    // 3초 후 자동 제거
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s cubic-bezier(0.4, 0, 0.2, 1)';
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 3000);
}

// 유틸리티 함수들
const Utils = {
    // 숫자 포맷팅 (천 단위 콤마)
    formatNumber: function(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },
    
    // 가격 포맷팅
    formatPrice: function(price) {
        return '$' + this.formatNumber(price);
    },
    
    // 퍼센트 포맷팅
    formatPercent: function(percent) {
        const sign = percent >= 0 ? '+' : '';
        return sign + percent.toFixed(2) + '%';
    },
    
    // 날짜 포맷팅
    formatDate: function(date) {
        const d = new Date(date);
        const month = d.getMonth() + 1;
        const day = d.getDate();
        const hour = d.getHours();
        const minute = d.getMinutes();
        
        return `${month}월 ${day}일 ${hour}:${minute.toString().padStart(2, '0')}`;
    }
};

// 전역에서 사용 가능하도록 export
window.Utils = Utils;
window.showNotification = showNotification;
