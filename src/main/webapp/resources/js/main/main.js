/* ===========================
   메인 페이지 JavaScript (com.antmillion.main.js)
   =========================== */

// Mock 데이터 (상위 6개만)
const mainStocks = [
    { id: 1, name: '삼성전자', logo: 'samsung', price: '140,900원', change: '+1.43%', isPositive: true, buyRatio: 50, sellRatio: 50, isFavorite: false },
    { id: 2, name: 'SK하이닉스', logo: 'sk', price: '742,500원', change: '+2.27%', isPositive: true, buyRatio: 43, sellRatio: 57, isFavorite: true },
    { id: 3, name: '현대차', logo: 'hyundai', price: '326,500원', change: '+6.00%', isPositive: true, buyRatio: 75, sellRatio: 25, isFavorite: false },
    { id: 4, name: '한미반도체', logo: 'hanmi', price: '186,300원', change: '+1.41%', isPositive: true, buyRatio: 41, sellRatio: 59, isFavorite: false },
    { id: 5, name: 'KODEX 레버리지', logo: 'kodex', price: '57,300원', change: '+1.64%', isPositive: true, buyRatio: 44, sellRatio: 56, isFavorite: false }
];

// 로고 이미지 URL 매핑
const logoMap = {
    'samsung': 'https://via.placeholder.com/40?text=LOGO',
    'sk': 'https://via.placeholder.com/40?text=LOGO',
    'hyundai': 'https://via.placeholder.com/40?text=LOGO',
    'hanmi': 'https://via.placeholder.com/40?text=LOGO',
    'kodex': 'https://via.placeholder.com/40?text=LOGO',
    'doosan': 'https://via.placeholder.com/40?text=LOGO',
    'hanwha': 'https://via.placeholder.com/40?text=LOGO',
    'hyundai-motor': 'https://via.placeholder.com/40?text=LOGO'
};

// 날짜 포멧 변경
function formatDate(yyyymmdd) {
    return {
        year: Number(yyyymmdd.substring(0, 4)),
        month: Number(yyyymmdd.substring(4, 6)),
        day: Number(yyyymmdd.substring(6, 8)),
    };
}

// 종목 아이템 HTML 생성 함수
function createMainStockItemHTML(stock, index) {
    const changeClass = stock.isPositive ? 'positive' : 'negative';
    const favoriteIcon = stock.isFavorite ? '♥' : '♡';
    const favoriteClass = stock.isFavorite ? 'active' : '';
    const logoUrl = logoMap[stock.logo] || 'https://via.placeholder.com/40?text=LOGO';

    return `
        <div class="main-stocklist-item" data-id="${stock.id}">
            <div class="main-stocklist-favorite">
                <span class="main-stocklist-rank">${index + 1}</span>
                <button class="main-stocklist-favorite-btn ${favoriteClass}" data-id="${stock.id}">${favoriteIcon}</button>
            </div>
            <div class="main-stocklist-info">
                <div class="main-stocklist-logo">
                    <img src="${logoUrl}" alt="${stock.name}">
                </div>
                <span class="main-stocklist-name">${stock.name}</span>
            </div>
            <div class="main-stocklist-price">${stock.price}</div>
            <div class="main-stocklist-change ${changeClass}">${stock.change}</div>
            <div class="main-stocklist-sentiment">
                <div class="main-stocklist-sentiment-bar">
                    <div class="main-stocklist-sentiment-buy" style="width: ${stock.buyRatio}%;"></div>
                    <div class="main-stocklist-sentiment-sell" style="width: ${stock.sellRatio}%;"></div>
                </div>
                <div class="main-stocklist-sentiment-labels">
                    <span class="main-stocklist-sentiment-buy-label">${stock.buyRatio}</span>
                    <span class="main-stocklist-sentiment-sell-label">${stock.sellRatio}</span>
                </div>
            </div>
        </div>
    `;
}

// 종목 리스트 렌더링 함수
function renderMainStocks() {
    const container = document.getElementById('main-stocklist-Container');
    if (!container) return;

    // HTML 생성
    const html = mainStocks.map((stock, index) => createMainStockItemHTML(stock, index)).join('');
    container.innerHTML = html;

    // 이벤트 리스너 재등록
    attachMainFavoriteListeners();
    attachMainStockItemListeners();
}

// 즐겨찾기 버튼 이벤트 리스너 등록
function attachMainFavoriteListeners() {
    document.querySelectorAll('.main-stocklist-favorite-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const stockId = parseInt(this.getAttribute('data-id'));

            // 데이터 업데이트
            const stock = mainStocks.find(s => s.id === stockId);
            if (stock) {
                stock.isFavorite = !stock.isFavorite;

                this.classList.toggle('active');
                this.textContent = stock.isFavorite ? '♥' : '♡';
            }
        });
    });
}

// 종목 아이템 클릭 이벤트 리스너 등록
function attachMainStockItemListeners() {

    document.querySelectorAll('.main-stocklist-item').forEach((item, index) => {
        // 첫 번째 항목을 active로 설정
        if (index === 0) {
            item.classList.add('main-active');
        }
        
        // mouseenter 이벤트 추가
        item.addEventListener('mouseenter', function(e) {
            // 즐겨찾기 버튼 클릭은 제외
            if (e.target.closest('.main-stocklist-favorite-btn')) {
                return;
            }
            
            // 모든 항목의 active 제거
            document.querySelectorAll('.main-stocklist-item').forEach(i => i.classList.remove('main-active'));
            
            // 현재 항목을 active로 설정
            this.classList.add('main-active');
            
            // 종목명 가져오기
            const stockName = this.querySelector('.main-stocklist-name').textContent;
            
            // 차트 헤더 업데이트
            const chartStockName = document.querySelector('.main-chart-stock-name');
            if (chartStockName) {
                chartStockName.textContent = stockName;
            }
            
            // 차트 업데이트

        });
        
        // click 이벤트 추가
        item.addEventListener('click', function(e) {
            // 즐겨찾기 버튼 클릭은 제외
            if (e.target.closest('.main-stocklist-favorite-btn')) {
                return;
            }

            // contextPath는 JSP에서 전역 변수로 설정되어 있음
            window.location.href = contextPath + '/stock/detail';
        });
    });
}

// 차트 인스턴스 저장
let kospiChart = null;
let kosdaqChart = null;

// DOM이 로드되면 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeMainPage();
});

function initializeMainPage() {
    // 종목 리스트 렌더링
    renderMainStocks();
    
    // 차트 초기화
    initializeCharts();
    
    // 미션 버튼 이벤트
    initializeMissionButton();
}

// 차트 초기화 (TradingView Lightweight Charts 사용)
function initializeCharts() {
    //코스피 차트
    drawMarketIndexChart(
        '.main-grid-kospi',
        '#main-kospi-chart',
        contextPath + '/api/kis/marketIndex/0001'
    );
    //코스닥 차트
    drawMarketIndexChart(
        '.main-grid-kosdaq',
        '#main-kosdaq-chart',
        contextPath + '/api/kis/marketIndex/1001'
    );
}

//코스피/코스닥 차트
function drawMarketIndexChart(cardSelector, chartId, apiUrl) {
    const card = document.querySelector(cardSelector);
    const chartEl = card.querySelector(chartId);
    const chart = LightweightCharts.createChart(chartEl, {
        width: document.getElementById('main-kospi-chart').clientWidth,
        height: document.getElementById('main-kospi-chart').clientHeight,
        layout: {
            background: {type: 'solid', color: 'white'},
            textColor: 'black'
        },
        grid: {
            vertLines: { color: '#eee' },
            horzLines: { color: '#eee' }
        },
        timeScale: {
            borderColor: '#cccccc'
        }
    });
    const candleSeries = chart.addSeries(
        LightweightCharts.CandlestickSeries,
        {
            upColor: '#e74c3c',
            downColor: '#3498db',
            borderUpColor: '#e74c3c',
            borderDownColor: '#3498db',
            wickUpColor: '#e74c3c',
            wickDownColor: '#3498db'
        }
    );

    fetch(apiUrl)
        .then(res => res.json())
        .then(data => {
            renderMarketIndexHeader(card, Number(data[0].bstp_nmix_prpr), Number(data[1].bstp_nmix_prpr))
            data.sort((a, b) => a.stck_bsop_date.localeCompare(b.stck_bsop_date))
            const chartData = data.map(row => ({
                time: formatDate(row.stck_bsop_date),
                open: Number(row.bstp_nmix_oprc),
                high: Number(row.bstp_nmix_hgpr),
                low: Number(row.bstp_nmix_lwpr),
                close: Number(row.bstp_nmix_prpr)
            }));

            candleSeries.setData(chartData);
            chart.timeScale().fitContent();
        });
}

//코스피/코스닥 차트 헤더 변경 - 현재 수치, 전일대비
function renderMarketIndexHeader(cardEl, today, yesterday) {
    const presentPriceEl = cardEl.querySelector('.main-market-index-price');
    const changeValueEl = cardEl.querySelector('.main-change-value');
    const changePercentEl = cardEl.querySelector('.main-change-percent');

    const diff =  today - yesterday;
    const diffPercent = (diff / yesterday) * 100;

    presentPriceEl.textContent = today+'';
    changeValueEl.textContent = `${diff > 0 ? '+' : ''}${diff.toFixed(2)}`;
    changePercentEl.textContent = `${diff > 0 ? '+' : ''}${diffPercent.toFixed(2)}%`;
    const isPositive = diff > 0;
    [changeValueEl, changePercentEl].forEach(el => {
        el.classList.toggle('main-positive', isPositive);
        el.classList.toggle('main-negative', !isPositive);
    });
}

// 미션 버튼 이벤트
function initializeMissionButton() {
    const missionButton = document.querySelector('.main-mission-button');
    
    if (missionButton) {
        missionButton.addEventListener('click', function() {
            // 미션 페이지로 이동 또는 모달 표시
            window.showNotification('미션 페이지로 이동합니다...', 'info');
            
            // 실제로는 다음과 같이 페이지 이동
            setTimeout(() => {
                // window.location.href = 'mission.jsp';
            }, 1000);
        });
    }
}
