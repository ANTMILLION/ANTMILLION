/* ===========================
   메인 페이지 JavaScript (com.antmillion.main.js)
   =========================== */

var subscribedTopics = {}; // 구독한 토픽 저장 {stockCode: subscription}

// STOMP 연결 (수정 버전)
function connectStomp() {
    var url = contextPath + '/ws-stomp';
    var socket = new SockJS(url);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('STOMP 연결 성공: ' + frame);

        // ✅ 수정: 연결 성공 후 종목 리스트 가져오기
        // (종목 리스트 렌더링 안에서 구독이 자동으로 이뤄짐)
        renderMainStocks();
    }, function(error) {
        console.error('STOMP 연결 실패: ' + error);
        // 5초 후 재연결 시도
        setTimeout(connectStomp, 5000);
    });
}

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
    const favoriteIcon = stock.isFavorite ? '♥' : '♡';
    const favoriteClass = stock.isFavorite ? 'active' : '';
    const currentPrice = Number(stock.stck_prpr).toLocaleString('ko-KR') + '원';

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
            <div class="main-stocklist-price">${currentPrice}</div>
            <div class="main-stocklist-change">-</div>
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
            // 관심종목 목록 가져오기
            return fetch(contextPath + '/api/interest/list')
                .then(res => res.json())
                .then(interestCodes => {
                    const html = data
                        .slice(0, 5)
                        .map((stock, index) => {
                            // 관심종목 여부 확인
                            stock.isFavorite = interestCodes.includes(stock.mksc_shrn_iscd);
                            return createMainStockItemHTML(stock, index);
                        }).join('');
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

                        //1번 항목의 차트 그리기
                        drawStockMinuteChart(firstStock.mksc_shrn_iscd);
                    }

                    // 종목 리스트 렌더링 완료 후 웹소켓 구독
                    if (!isMockMode) {
                        const stockCodes = data.slice(0, 5).map(stock => stock.mksc_shrn_iscd);
                        subscribeAllStocksToBackend(stockCodes);
                    }
                });
        });
}

// 즐겨찾기 버튼 이벤트 리스너 등록
function attachMainFavoriteListeners() {
    document.querySelectorAll('.main-stocklist-favorite-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const stockCode = this.getAttribute('data-id');

            // 서버에 관심종목 토글 요청
            fetch(contextPath + '/api/interest/toggle', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ stockCode: stockCode })
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        // UI 업데이트
                        this.classList.toggle('active');
                        this.textContent = data.isInterest ? '♥' : '♡';
                    }
                })
                .catch(error => {
                    console.error('관심종목 토글 실패:', error);
                });
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
    connectStomp();

    // ✅ 레이아웃 완전 확정 후 차트 초기화
    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            initializeCharts();
        });
    });


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
    
    // 차트 생성 후 강제로 resize 이벤트 발생
    setTimeout(() => {
        window.dispatchEvent(new Event('resize'));
    }, 100);
}

//코스피/코스닥 차트
function drawMarketIndexChart(cardSelector, chartId, apiUrl) {
    const card = document.querySelector(cardSelector);
    const chartEl = card.querySelector(chartId);
    const container = chartEl.parentElement;
    
    const chart = LightweightCharts.createChart(chartEl, {
        width: container.clientWidth,
        height: container.clientHeight,
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
        },
        rightPriceScale: {
            visible: true,
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

            setTimeout(() => {
                chart.resize(container.clientWidth, container.clientHeight);
            }, 50);
        });
    chartEl._chart = chart;
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

// 차트 반응형
window.addEventListener('resize', () => {
    // 코스피 차트
    const kospiChartEl = document.getElementById('main-kospi-chart');
    if (kospiChartEl && kospiChartEl._chart) {
        const container = kospiChartEl.parentElement;
        kospiChartEl._chart.resize(container.clientWidth, container.clientHeight);
    }

    // 코스닥 차트
    const kosdaqChartEl = document.getElementById('main-kosdaq-chart');
    if (kosdaqChartEl && kosdaqChartEl._chart) {
        const container = kosdaqChartEl.parentElement;
        kosdaqChartEl._chart.resize(container.clientWidth, container.clientHeight);
    }
    if (stockChart) {
        const container = document.getElementById('main-stockChart');
        stockChart.resize(container.clientWidth, container.clientHeight);
    }
});


// 모든 종목 백엔드 구독 요청
function subscribeAllStocksToBackend(stockCodes) {
    fetch(contextPath + '/api/kis/websocket/subscribe-multiple?trId=H0STCNT0', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(stockCodes)
    })
        .then(res => res.json())
        .then(response => {
            console.log('백엔드 구독 완료:', response);

            // 백엔드 구독 성공 후, 프론트엔드에서 각 종목 STOMP 토픽 구독
            stockCodes.forEach(stockCode => {
                subscribeStockTopic(stockCode);
            });
        })
        .catch(error => {
            console.error('백엔드 구독 실패:', error);
        });
}

// 개별 종목 STOMP 토픽 구독
function subscribeStockTopic(stockCode) {
    if (subscribedTopics[stockCode]) {
        console.log('이미 구독 중:', stockCode);
        return;
    }

    const topic = '/topic/kis-trade/present' + stockCode;
    const subscription = stompClient.subscribe(topic, function(message) {
        const tradeData = JSON.parse(message.body);
        console.log('실시간 데이터 수신:', tradeData);

        // 화면 업데이트
        updateStockRealtimePrice(stockCode, tradeData);
    });

    subscribedTopics[stockCode] = subscription;
    console.log('토픽 구독 완료:', topic);
}

// 실시간 가격 화면 업데이트
function updateStockRealtimePrice(stockCode, tradeData) {
    // 해당 종목의 DOM 요소 찾기
    const stockItem = document.querySelector(`.main-stocklist-item[data-id="${stockCode}"]`);
    if (!stockItem) return;

    // 현재가 업데이트
    const priceElement = stockItem.querySelector('.main-stocklist-price');
    if (priceElement) {
        const price = Number(tradeData.stckPrpr).toLocaleString('ko-KR') + '원';
        priceElement.textContent = price;
    }

    // 등락률 업데이트 (main-stocklist-change가 등락률을 표시한다고 가정)
    const changeElement = stockItem.querySelector('.main-stocklist-change');
    if (changeElement) {
        // 1,2: 상승(+), 3: 보합(0), 4,5: 하락(-)
        let sign = '';
        let signClass = '';

        if (tradeData.prdySign === '1' || tradeData.prdySign === '2') {
            sign = '+';
            signClass = 'positive';
        } else if (tradeData.prdySign === '4' || tradeData.prdySign === '5') {
            sign = '';
            signClass = 'negative';
        } else {
            sign = '';
            signClass = '';
        }

        changeElement.textContent = sign + tradeData.prdyCtrt + '%';

        // 색상 변경
        changeElement.classList.remove('positive', 'negative');
        if (signClass) {
            changeElement.classList.add(signClass);
        }
    }

    // 거래 비율 업데이트 (매수 비율 있으면)
    if (tradeData.shnuRate) {
        const buyRate = parseFloat(tradeData.shnuRate) * 100;
        const sellRate = 100 - buyRate;

        const buyBar = stockItem.querySelector('.main-stocklist-sentiment-buy');
        const sellBar = stockItem.querySelector('.main-stocklist-sentiment-sell');
        const buyLabel = stockItem.querySelector('.main-stocklist-sentiment-buy-label');
        const sellLabel = stockItem.querySelector('.main-stocklist-sentiment-sell-label');

        if (buyBar && sellBar) {
            buyBar.style.width = buyRate + '%';
            sellBar.style.width = sellRate + '%';
        }

        if (buyLabel && sellLabel) {
            buyLabel.textContent = Math.round(buyRate);
            sellLabel.textContent = Math.round(sellRate);
        }
    }
}

// 페이지 떠날 때 전체 구독 해제
function unsubscribeAllStocks() {
    console.log('구독 해제 시작...');

    // 프론트엔드 STOMP 구독 해제
    for (let stockCode in subscribedTopics) {
        if (subscribedTopics[stockCode]) {
            subscribedTopics[stockCode].unsubscribe();
            console.log('토픽 구독 해제:', stockCode);
        }
    }
    subscribedTopics = {};

    // 백엔드 구독 해제
    fetch(contextPath + '/api/kis/websocket/unsubscribe-all?trId=H0STCNT0', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        // keepalive: true는 페이지 종료 시에도 요청이 완료되도록 보장
        keepalive: true
    })
        .then(res => res.json())
        .then(response => {
            console.log('백엔드 구독 해제 완료:', response);
        })
        .catch(error => {
            console.error('백엔드 구독 해제 실패:', error);
        });

    // STOMP 연결 종료
    if (stompClient !== null && stompClient.connected) {
        stompClient.disconnect(function() {
            console.log('STOMP 연결 종료');
        });
    }
}

// beforeunload: 브라우저 닫기, 새로고침, 다른 페이지 이동
window.addEventListener('beforeunload', function(e) {
    unsubscribeAllStocks();
});

// pagehide: 모바일 환경에서도 작동
window.addEventListener('pagehide', function(e) {
    unsubscribeAllStocks();
});

// SPA 환경에서 다른 화면으로 이동하는 경우 사용할 함수
function navigateToOtherPage(url) {
    unsubscribeAllStocks();

    // 구독 해제 후 페이지 이동
    setTimeout(() => {
        window.location.href = url;
    }, 100);
}

// ========== Mock 데이터 제어 함수 (main.js 맨 아래 추가) ==========

let isMockMode = false; // Mock 모드 플래그

// Mock 모드로 전환 (한투 구독 건너뛰고 Mock만 사용)
function enableMockMode() {
    isMockMode = true;
    console.log('Mock 모드 활성화');

    // 현재 화면에 표시된 종목 리스트 가져오기
    const stockItems = document.querySelectorAll('.main-stocklist-item');
    const mockStocks = [];

    stockItems.forEach(item => {
        const stockCode = item.getAttribute('data-id');
        const priceText = item.querySelector('.main-stocklist-price').textContent;
        const basePrice = parseInt(priceText.replace(/[^0-9]/g, ''));

        mockStocks.push({
            stockCode: stockCode,
            basePrice: basePrice
        });
    });

    // Mock 종목 등록
    fetch(contextPath + '/api/kis/mock/add-stocks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(mockStocks)
    })
        .then(res => res.json())
        .then(response => {
            console.log('Mock 종목 등록:', response);

            // Mock 데이터 전송 시작
            return fetch(contextPath + '/api/kis/mock/start', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        })
        .then(res => res.json())
        .then(response => {
            console.log('Mock 데이터 시작:', response);

            // STOMP 구독만 진행 (한투 백엔드 구독은 건너뜀)
            const stockCodes = mockStocks.map(s => s.stockCode);
            stockCodes.forEach(stockCode => {
                subscribeStockTopic(stockCode);
            });

            alert('테스트 모드 시작! 한투 연결 없이 가짜 데이터로 테스트합니다.');
        })
        .catch(error => {
            console.error('Mock 모드 활성화 실패:', error);
        });
}

// Mock 모드 비활성화
function disableMockMode() {
    isMockMode = false;

    // Mock 데이터 중지
    fetch(contextPath + '/api/kis/mock/stop', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(res => res.json())
        .then(response => {
            console.log('Mock 데이터 중지:', response);

            // Mock 종목 제거
            return fetch(contextPath + '/api/kis/mock/clear', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        })
        .then(res => res.json())
        .then(response => {
            console.log('Mock 종목 제거:', response);
            alert('테스트 모드 종료! 실제 모드로 전환하려면 페이지를 새로고침하세요.');
        })
        .catch(error => {
            console.error('Mock 모드 비활성화 실패:', error);
        });
}

// 브라우저 콘솔에서 사용 가능하도록 전역으로 노출
window.enableMockMode = enableMockMode;
window.disableMockMode = disableMockMode;
