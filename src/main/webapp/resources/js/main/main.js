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
                    <div class="main-stocklist-sentiment-buy" style="width: 50%;"></div>
                    <div class="main-stocklist-sentiment-sell" style="width: 50%;"></div>
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

// 일별 분봉 조회 - 차트
let stockChart = null;
let candleSeries = null;

// 시간 포맷 변환 함수
function formatToTimestamp(dateStr, timeStr) {
    const year = parseInt(dateStr.substring(0, 4));
    const month = parseInt(dateStr.substring(4, 6)) - 1;
    const day = parseInt(dateStr.substring(6, 8));
    const hour = parseInt(timeStr.substring(0, 2));
    const minute = parseInt(timeStr.substring(2, 4));
    const second = parseInt(timeStr.substring(4, 6)) || 0;

    const date = new Date(year, month, day, hour, minute, second); // KST 시간대 기준
    const offsetInSeconds = date.getTimezoneOffset() * 60;
    return Math.floor(date.getTime() / 1000) - offsetInSeconds;
}

// 차트 그리기 함수
function drawStockMinuteChart(stockCode) {
    const chartContainer = document.getElementById('main-stockChart');
    if (!chartContainer) return;
    chartContainer.innerHTML = ''; // 기존 차트가 있다면 삭제

    stockChart = LightweightCharts.createChart(chartContainer, {
        width: chartContainer.clientWidth,
        height: chartContainer.clientHeight,
        localization: {
            locale: 'ko-KR',
            timeFormatter: (time) => {
                const date = new Date((time - 9 * 60 * 60) * 1000);
                const y = date.getFullYear();
                const m = date.getMonth() + 1;
                const d = date.getDate();
                const h = String(date.getHours()).padStart(2, '0');
                const min = String(date.getMinutes()).padStart(2, '0');
                return `${y}년 ${m}월 ${d}일 ${h}:${min}`;
            },
        },
        timeScale: {
            timeVisible: true, // 시간 표시
            secondsVisible: false,
            barSpacing: 10,
        },
        layout: {
            background: {type: 'solid', color: 'white'},
            textColor: 'black'
        },

    });

    candleSeries = stockChart.addSeries(LightweightCharts.CandlestickSeries, {
        upColor: '#e74c3c',
        downColor: '#3498db',
        borderUpColor: '#e74c3c',
        borderDownColor: '#3498db',
        wickUpColor: '#e74c3c',
        wickDownColor: '#3498db'
    });

    // API 호출
	fetch(contextPath + `/api/kis/stream/${stockCode}`)
    .then(res => res.json())
    .then(data => {
        if (!data || data.length === 0) {
            console.error("데이터가 비어있음");
            return;
        }

        // 데이터 정렬
        data.sort((a, b) => (a.stck_bsop_date + a.stck_cntg_hour).localeCompare(b.stck_bsop_date + b.stck_cntg_hour));

        const chartData = [];
        const seenTimes = new Set();

        data.forEach(row => {
            const timestamp = formatToTimestamp(row.stck_bsop_date, row.stck_cntg_hour);
            
            // 중복 시간 데이터 제거
            if (!seenTimes.has(timestamp)) {
                chartData.push({
                    time: timestamp,
                    open: Number(row.stck_oprc),
                    high: Number(row.stck_hgpr),
                    low: Number(row.stck_lwpr),
                    close: Number(row.stck_prpr)
                });
                seenTimes.add(timestamp);
            }
        });

        console.log("변환된 차트 데이터:", chartData);
        
        if (chartData.length > 0) {
            candleSeries.setData(chartData);
            stockChart.timeScale().fitContent();
        }
    })
    .catch(err => console.error("API 호출 에러:", err));
}

// 마우스 오버 이벤트
document.addEventListener('mouseover', (e) => {
    const item = e.target.closest('.main-stocklist-item');
    if (item) {
        const stockCode = item.dataset.id; 
        const stockName = item.querySelector('.main-stock-name')?.textContent || "종목명";

        if (stockCode) {
            const nameEl = document.getElementById('displayStockName');
            const codeEl = document.getElementById('displayStockCode');
            
            // 현재 그려진 차트와 코드가 다를 때만 새로 그리기
            if (codeEl.textContent !== stockCode) {
                nameEl.textContent = stockName;
                codeEl.textContent = stockCode;
                drawStockMinuteChart(stockCode);
            }
        }
    }
});

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    // HTML 헤더에 이미 적혀있는 종목 코드를 읽어옴
    const defaultCodeEl = document.getElementById('displayStockCode');
    
    if (defaultCodeEl) {
        const defaultStockCode = defaultCodeEl.textContent.trim();
        console.log("초기 종목 코드 로드:", defaultStockCode);
        
        // 읽어온 코드로 차트 그리기 실행
        drawStockMinuteChart(defaultStockCode);
    }
});

// 차트 반응형
window.addEventListener('resize', () => {
    if (stockChart) {
        const container = document.getElementById('main-stockChart');
        stockChart.resize(container.clientWidth, container.clientHeight);
    }
});
