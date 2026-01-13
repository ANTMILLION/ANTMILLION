/* ===========================
   종목 리스트 페이지 JavaScript
   =========================== */

// Mock 데이터
const allStocks = [
    { id: 1, name: '삼성전자', logo: 'samsung', price: '140,900원', change: '+1.43%', isPositive: true, buyRatio: 50, sellRatio: 50, isFavorite: false },
    { id: 2, name: 'SK하이닉스', logo: 'sk', price: '742,500원', change: '+2.27%', isPositive: true, buyRatio: 43, sellRatio: 57, isFavorite: true },
    { id: 3, name: '현대차', logo: 'hyundai', price: '326,500원', change: '+6.00%', isPositive: true, buyRatio: 75, sellRatio: 25, isFavorite: false },
    { id: 4, name: '한미반도체', logo: 'hanmi', price: '186,300원', change: '+1.41%', isPositive: true, buyRatio: 41, sellRatio: 59, isFavorite: false },
    { id: 5, name: 'KODEX 레버리지', logo: 'kodex', price: '57,300원', change: '+1.64%', isPositive: true, buyRatio: 44, sellRatio: 56, isFavorite: false },
    { id: 6, name: '두산에너빌리티', logo: 'doosan', price: '84,900원', change: '-1.16%', isPositive: false, buyRatio: 57, sellRatio: 43, isFavorite: true },
    { id: 7, name: '한화오션', logo: 'hanwha', price: '123,500원', change: '+3.08%', isPositive: true, buyRatio: 66, sellRatio: 34, isFavorite: false },
    { id: 8, name: 'KODEX 200', logo: 'kodex', price: '66,765원', change: '+0.83%', isPositive: true, buyRatio: 29, sellRatio: 71, isFavorite: false },
    { id: 9, name: '현대오토에버', logo: 'hyundai-motor', price: '336,000원', change: '+7.69%', isPositive: true, buyRatio: 66, sellRatio: 34, isFavorite: true },
    { id: 10, name: 'KODEX 200선물...', logo: 'kodex', price: '509원', change: '-1.54%', isPositive: false, buyRatio: 44, sellRatio: 56, isFavorite: false },
    { id: 11, name: 'SFA반도체', logo: 'sfa', price: '6,090원', change: '+8.17%', isPositive: true, buyRatio: 46, sellRatio: 54, isFavorite: false },
    { id: 12, name: 'NAVER', logo: 'naver', price: '255,000원', change: '-1.92%', isPositive: false, buyRatio: 52, sellRatio: 48, isFavorite: true }
];

let currentTab = 'all';

// DOM 로드 후 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeStockList();
});

function initializeStockList() {
    renderStockList('all');
    renderStockList('favorite');
    initializeTabButtons();
}

// 탭 버튼 초기화
function initializeTabButtons() {
    const tabButtons = document.querySelectorAll('.stocklist-tab-btn');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tab = this.getAttribute('data-tab');
            switchTab(tab);
        });
    });
}

// 탭 전환
function switchTab(tab) {
    currentTab = tab;
    
    // 탭 버튼 활성화 상태 변경
    const tabButtons = document.querySelectorAll('.stocklist-tab-btn');
    tabButtons.forEach(button => {
        if (button.getAttribute('data-tab') === tab) {
            button.classList.add('active');
        } else {
            button.classList.remove('active');
        }
    });
    
    // 리스트 표시/숨김
    const allList = document.getElementById('stocklist-all');
    const favoriteList = document.getElementById('stocklist-favorite');
    
    if (tab === 'all') {
        allList.classList.remove('hidden');
        favoriteList.classList.add('hidden');
    } else {
        allList.classList.add('hidden');
        favoriteList.classList.remove('hidden');
    }
}

// 종목 리스트 렌더링
function renderStockList(type) {
    const container = document.getElementById(`stocklist-${type}`);
    if (!container) return;
    
    const stocks = type === 'all' ? allStocks : allStocks.filter(stock => stock.isFavorite);
    
    container.innerHTML = stocks.map((stock, index) => `
        <div class="stocklist-item" data-stock-id="${stock.id}">
            <div class="stocklist-left">
                <button class="stocklist-favorite-btn ${stock.isFavorite ? 'active' : ''}" onclick="toggleFavorite(${stock.id})">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2">
                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                    </svg>
                </button>
                <div class="stocklist-rank">${index + 1}</div>
                <div class="stocklist-company">
                    <div class="stocklist-company-logo ${stock.logo}">
                        ${getCompanyLogoText(stock.name)}
                    </div>
                    <div class="stocklist-company-info">
                        <div class="stocklist-company-name">${stock.name}</div>
                    </div>
                </div>
            </div>
            <div class="stocklist-right">
                <div class="stocklist-price">${stock.price}</div>
                <div class="stocklist-change ${stock.isPositive ? 'positive' : 'negative'}">${stock.change}</div>
                <div class="stocklist-ratio-bar">
                    <div class="stocklist-ratio-value-buy">${stock.buyRatio}</div>
                    <div class="stocklist-ratio-bar-container">
                        <div class="stocklist-ratio-bar-buy" style="width: ${stock.buyRatio}%"></div>
                        <div class="stocklist-ratio-bar-sell" style="width: ${stock.sellRatio}%"></div>
                    </div>
                    <div class="stocklist-ratio-value-sell">${stock.sellRatio}</div>
                </div>
            </div>
        </div>
    `).join('');
}

// 회사 로고 텍스트 생성 (회사명 첫 글자들)
function getCompanyLogoText(name) {
    if (name.includes('KODEX') || name.includes('SFA')) {
        return name.substring(0, 2);
    }
    if (name.includes('NAVER')) {
        return 'N';
    }
    return name.substring(0, 2);
}

// 관심 종목 토글
function toggleFavorite(stockId) {
    const stock = allStocks.find(s => s.id === stockId);
    if (stock) {
        stock.isFavorite = !stock.isFavorite;
        
        // 두 리스트 모두 다시 렌더링
        renderStockList('all');
        renderStockList('favorite');
    }
}

// 종목 클릭 이벤트 (추후 구현)
document.addEventListener('click', function(e) {
    const stockItem = e.target.closest('.stocklist-item');
    if (stockItem && !e.target.closest('.stocklist-favorite-btn')) {
        const stockId = stockItem.getAttribute('data-stock-id');
        console.log('종목 클릭:', stockId);
        // TODO: 종목 상세 페이지로 이동
    }
});
