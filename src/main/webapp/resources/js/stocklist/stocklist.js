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

// 로고 이미지 URL 매핑
const logoMap = {
    'samsung': 'https://via.placeholder.com/40?text=LOGO',
    'sk': 'https://via.placeholder.com/40?text=LOGO',
    'hyundai': 'https://via.placeholder.com/40?text=LOGO',
    'hanmi': 'https://via.placeholder.com/40?text=LOGO',
    'kodex': 'https://via.placeholder.com/40?text=LOGO',
    'doosan': 'https://via.placeholder.com/40?text=LOGO',
    'hanwha': 'https://via.placeholder.com/40?text=LOGO',
    'hyundai-motor': 'https://via.placeholder.com/40?text=LOGO',
    'sfa': 'https://via.placeholder.com/40?text=LOGOA',
    'naver': 'https://via.placeholder.com/40?text=LOGON'
};

// 종목 아이템 HTML 생성 함수
function createStockItemHTML(stock, index) {
    const changeClass = stock.isPositive ? 'positive' : 'negative';
    const favoriteIcon = stock.isFavorite ? '♥' : '♡';
    const favoriteClass = stock.isFavorite ? 'active' : '';
    const logoUrl = logoMap[stock.logo] || 'https://via.placeholder.com/40?text=LOGO';

    return `
        <div class="stocklist-item" data-id="${stock.id}">
            <div class="stocklist-favorite">
                <span class="stocklist-rank">${index + 1}</span>
                <button class="stocklist-favorite-btn ${favoriteClass}" data-id="${stock.id}">${favoriteIcon}</button>
            </div>
            <div class="stocklist-info">
                <div class="stocklist-logo">
                    <img src="${logoUrl}" alt="${stock.name}">
                </div>
                <span class="stocklist-name">${stock.name}</span>
            </div>
            <div class="stocklist-price">${stock.price}</div>
            <div class="stocklist-change ${changeClass}">${stock.change}</div>
            <div class="stocklist-sentiment">
                <span class="stocklist-sentiment-label">매수</span>
                <div class="stocklist-sentiment-bar">
                    <div class="stocklist-sentiment-buy" style="width: ${stock.buyRatio}%;">${stock.buyRatio}</div>
                    <div class="stocklist-sentiment-sell" style="width: ${stock.sellRatio}%;">${stock.sellRatio}</div>
                </div>
            </div>
        </div>
    `;
}

// 종목 리스트 렌더링 함수
function renderStocks() {
    const container = document.getElementById('stocklist-Container');

    // 필터링: 전체 or 즐겨찾기
    let filteredStocks = allStocks;
    if (currentTab === 'favorite') {
        filteredStocks = allStocks.filter(stock => stock.isFavorite);
    }

    // HTML 생성
    const html = filteredStocks.map((stock, index) => createStockItemHTML(stock, index)).join('');
    container.innerHTML = html;

    // 이벤트 리스너 재등록
    attachFavoriteListeners();

    // 렌더링 후 현재 토글 상태에 맞춰 거래 비율 표시 여부 동기화
    syncSentimentDisplay();
}

// 즐겨찾기 버튼 이벤트 리스너 등록
function attachFavoriteListeners() {
    document.querySelectorAll('.stocklist-favorite-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const stockId = parseInt(this.getAttribute('data-id'));

            // 데이터 업데이트
            const stock = allStocks.find(s => s.id === stockId);
            if (stock) {
                stock.isFavorite = !stock.isFavorite;

                this.classList.toggle('active');
                this.textContent = stock.isFavorite ? '♥' : '♡';

                if (currentTab === 'favorite') {
                    renderStocks();
                }
            }
        });
    });
}

// 거래 비율 표시 동기화 함수 (토글 상태 체크)
function syncSentimentDisplay() {
    const toggle = document.getElementById('stocklist-sentimentToggle');
    const sentimentBars = document.querySelectorAll('.stocklist-sentiment');

    // 토글이 있으면 체크 여부에 따라 display 설정
    if (toggle) {
        const displayValue = toggle.checked ? 'flex' : 'none';
        sentimentBars.forEach(bar => {
            bar.style.display = displayValue;
        });
    }
}

// 탭 전환 이벤트
document.querySelectorAll('.stocklist-tab-btn').forEach((btn, index) => {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.stocklist-tab-btn').forEach(b => b.classList.remove('active'));
        this.classList.add('active');

        // 첫 번째 버튼: 전체종목, 두 번째 버튼: 관심종목
        currentTab = index === 0 ? 'all' : 'favorite';
        renderStocks();
    });
});

// 거래 비율 토글
const sentimentToggle = document.getElementById('stocklist-sentimentToggle');
sentimentToggle.addEventListener('change', function() {
    const sentimentBars = document.querySelectorAll('.stocklist-sentiment');
    sentimentBars.forEach(bar => {
        bar.style.display = this.checked ? 'flex' : 'none';
    });
});

// 초기 렌더링
document.addEventListener('DOMContentLoaded', function() {
    renderStocks();
});
