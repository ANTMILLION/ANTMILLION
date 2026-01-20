/* ===========================
   메인 페이지 JavaScript (com.antmillion.main.js)
   =========================== */

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
    //const changeClass = stock.isPositive ? 'positive' : 'negative';
    const favoriteIcon = stock.isFavorite ? '♥' : '♡';
    const favoriteClass = stock.isFavorite ? 'active' : '';
    //todo: 등락률 자리에 거래량을 임시로 넣어둠
    //todo: 즐겨찾기 일단 남겨둠
    //todo: 거래비율 자리에 50, 50 임시로 넣어둠
    return `
        <div class="main-stocklist-item" data-id="${stock.mksc_shrn_iscd}">
            <div class="main-stocklist-favorite">
                <span class="main-stocklist-rank">${index + 1}</span>
                <button class="main-stocklist-favorite-btn ${favoriteClass}" data-id="${stock.mksc_shrn_iscd}">${favoriteIcon}</button>
            </div>
            <div class="main-stocklist-info">
                <div class="main-stocklist-logo">
                    <img src="" alt="${stock.hts_kor_isnm}">
                </div>
                <span class="main-stocklist-name">${stock.hts_kor_isnm}</span>
            </div>
            <div class="main-stocklist-price">${stock.stck_prpr}</div>
            <div class="main-stocklist-change">${stock.acml_vol}</div>
            <div class="main-stocklist-sentiment">
                <div class="main-stocklist-sentiment-bar">
                    <div class="main-stocklist-sentiment-buy" style="width: ${stock.buyRatio}%;"></div>
                    <div class="main-stocklist-sentiment-sell" style="width: ${stock.sellRatio}%;"></div>
                </div>
                <div class="main-stocklist-sentiment-labels">
                    <span class="main-stocklist-sentiment-buy-label">50</span>
                    <span class="main-stocklist-sentiment-sell-label">50</span>
                </div>
            </div>
        </div>
    `;
}

// 종목 리스트 렌더링 함수
function renderMainStocks() {
    const container = document.getElementById('main-stocklist-Container');
    if (!container) return;

    fetch(contextPath + '/api/kis/volumeRank')
        .then(res => res.json())
        .then(data => {
            const html = data
                .slice(0, 5) //상위 5개만
                .map((stock, index) => createMainStockItemHTML(stock, index)).join('');
            container.innerHTML = html;
            // 이벤트 리스너 재등록
            attachMainFavoriteListeners();
            attachMainStockItemListeners();

            if (data.length > 0) {
                const firstStock = data[0];
                const chartStockName = document.querySelector('.main-chart-stock-name');
                const chartStockCode = document.querySelector('.main-chart-stock-code');

                if (chartStockName) {
                    chartStockName.textContent = firstStock.hts_kor_isnm;
                }
                if (chartStockCode) {
                    chartStockCode.textContent = firstStock.mksc_shrn_iscd;
                }
            }
        });


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
            const stockCode = this.getAttribute('data-id');
            const chartStockCode = document.querySelector('.main-chart-stock-code');
            const chartStockName = document.querySelector('.main-chart-stock-name');
            if (chartStockName) {
                chartStockName.textContent = stockName;
            }
            if (chartStockCode) {
                chartStockCode.textContent = stockCode;
            }
            
            // 차트 업데이트

        });
        
        // click 이벤트 추가
        item.addEventListener('click', function(e) {
            // 즐겨찾기 버튼 클릭은 제외
            if (e.target.closest('.main-stocklist-favorite-btn')) {
                return;
            }

            const stockId = this.getAttribute('data-id');
            // contextPath는 JSP에서 전역 변수로 설정되어 있음
            window.location.href = contextPath + '/stock/detail?code=' + stockId;
        });
    });
}

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
