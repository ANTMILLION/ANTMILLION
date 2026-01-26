// ===== 전역 변수 (community.js와 공유) =====
// const 대신 var 사용으로 전역 스코프에 등록
if (typeof contextPath === 'undefined') {
    var contextPath = '/antmillion';
    console.log('[contextPath 설정]', contextPath);
}

// STOMP 관련 변수
let stompClient = null;
let subscribedTopics = {}; // 구독한 토픽 저장 {stockCode: subscription}
let currentStockCodes = []; // 현재 화면에 표시된 종목 코드들

// ======= STOMP 연결 =======
function connectStomp() {
    const url = contextPath + '/ws-stomp';
    const socket = new SockJS(url);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('STOMP 연결 성공: ' + frame);

        // 연결 성공 후 현재 화면의 종목들 구독
        if (currentStockCodes.length > 0) {
            subscribeCurrentStocks();
        }
    }, function(error) {
        console.error('STOMP 연결 실패: ' + error);
        // 5초 후 재연결 시도
        setTimeout(connectStomp, 5000);
    });
}

// ======= 현재 상세 화면 종목 및 호가 구독 (시간차 적용 버전) =======
async function subscribeCurrentStocks() {
    // 기존 구독 모두 해제
    unsubscribeAllStocks();

    if (currentStockCodes.length === 0) return;

    // ✅ Mock 모드일 때는 한투 백엔드 구독 건너뛰고 STOMP만 구독
    if (isMockMode) {
        console.log('Mock 모드 - STOMP 구독 진행');
        currentStockCodes.forEach(stockCode => {
            subscribeStockTopic(stockCode);
            subscribeStockHoga(stockCode);
        });
        return;
    }

    try {
        // 1. 백엔드에 현재가 구독 요청 (H0UNCNT0)
        const presentRes = await fetch(contextPath + '/api/kis/websocket/subscribe-multiple?trId=H0UNCNT0', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(currentStockCodes)
        });
        console.log('현재가 백엔드 요청 완료');

        // ★ 핵심: 백엔드에서 웹소켓 세션이 안정화될 때까지 대기
        // .get()으로 블로킹되는 시간을 고려해 1.5초 정도 여유를 줌
        await new Promise(resolve => setTimeout(resolve, 1500));

        // 2. 백엔드에 호가 구독 요청 추가 (H0UNASP0)
        const hogaRes = await fetch(contextPath + '/api/kis/websocket/subscribe-multiple?trId=H0UNASP0', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(currentStockCodes)
        });
        console.log('호가 백엔드 요청 완료');

        // 3. 프론트엔드 STOMP 실제 구독 진행
        currentStockCodes.forEach(stockCode => {
            subscribeStockTopic(stockCode); // /topic/kis-trade/present{stockCode}
            subscribeStockHoga(stockCode);  // /topic/kis-trade/ask-bid{stockCode}
        });

        console.log('최종 STOMP 구독 프로세스 완료');

    } catch (error) {
        console.error('구독 프로세스 중 에러 발생:', error);
    }
}

// ========== 개별 종목 STOMP 토픽 구독 ==========
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

let isMarketOrder = false; 
let lastRealtimePrice = "";

// ========== 현재가 업데이트 함수 (확인용) ==========
function updateStockRealtimePrice(stockCode, tradeData) {
    const rawPrice = tradeData.stckPrpr;
    if (!rawPrice) return;
    
    lastRealtimePrice = rawPrice;
    const formattedPrice = Number(rawPrice).toLocaleString('ko-KR') + '원';

    const currentPriceH3 = document.getElementById('detail-current-price-h3');
    if (currentPriceH3) {
        currentPriceH3.textContent = formattedPrice;
        console.log("현재가 업데이트 성공:", formattedPrice);
    }

    if (isMarketOrder) {
        const orderPriceElem = document.getElementById('order-display-price');
        if (orderPriceElem) orderPriceElem.textContent = formattedPrice;
    }
}

// ======= 시장가/지정가 선택 로직 =======
function initPriceTypeEvents() {
    const tabLimit = document.querySelector('.detail-card-label span:first-child'); // 지정가
    const tabMarket = document.querySelector('.detail-card-label span:last-child');  // 시장가
    const bigPriceElement = document.querySelector('.detail-big-price');

    if (!tabLimit || !tabMarket || !bigPriceElement) return;

    // 시장가 클릭
    tabMarket.addEventListener('click', function() {
        isMarketOrder = true;
        tabMarket.style.color = '#1F2937';
        tabMarket.classList.add('detail-active-type');
        tabLimit.style.color = '#6B7280';
        tabLimit.classList.remove('detail-active-type');

        // 즉시 현재가로 변경
        if (lastRealtimePrice) {
            bigPriceElement.textContent = Number(lastRealtimePrice).toLocaleString() + '원';
        }
    });

    // 지정가 클릭
    tabLimit.addEventListener('click', function() {
        isMarketOrder = false;

        tabLimit.style.color = '#1F2937';
        tabLimit.classList.add('detail-active-type');
        tabMarket.style.color = '#6B7280';
        tabMarket.classList.remove('detail-active-type');
        
        // 지정가는 보통 초기값이나 사용자가 입력할 수 있는 상태로 둠 (원하는 값으로 세팅 가능)
        // bigPriceElement.textContent = "원래 지정가 값"; 
    });
}
    
// ========== 모든 구독 해제 ==========
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
    if (currentStockCodes.length > 0) {
        fetch(contextPath + '/api/kis/websocket/unsubscribe-all?trId=H0UNCNT0', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            keepalive: true
        })
            .then(res => res.json())
            .then(response => {
                console.log('백엔드 구독 해제 완료:', response);
            })
            .catch(error => {
                console.error('백엔드 구독 해제 실패:', error);
            });
    }
}

// ========== 호가 데이터 수신 및 UI 업데이트 ==========
function updateHogaUI(hogaData) {
    // 1. 호가 전용 컨테이너를 찾거나 생성함
    let hogaBox = document.querySelector('.detail-hoga-box');
    if (!hogaBox) return;

    let hogaList = hogaBox.querySelector('.hoga-container');
    if (!hogaList) {
        hogaBox.innerHTML = ''; // 처음 한 번만 비움
        hogaList = document.createElement('div');
        hogaList.className = 'hoga-container';
        hogaList.style.height = '100%';
        hogaBox.appendChild(hogaList);
    }

    hogaList.innerHTML = ''; // 호가 리스트만 초기화 (상단 현재가는 보존)

    // 2. 매도/매수 행 생성
    for (let i = 5; i >= 1; i--) {
        hogaList.appendChild(createHogaRow(hogaData[`askP${i}`], hogaData[`askPrsqn${i}`], 'ask'));
    }
    for (let i = 1; i <= 5; i++) {
        hogaList.appendChild(createHogaRow(hogaData[`bidP${i}`], hogaData[`bidPrsqn${i}`], 'bid'));
    }
}

// ========== 호가 행(Row) 생성 함수 (3분할 레이아웃) ==========
function createHogaRow(price, qty, type) {
    const row = document.createElement('div');
    row.className = `hoga-row ${type}-row`;
    
    // 클릭 시 가격 입력창 업데이트
    row.onclick = function() {
        if (!isMarketOrder) {
            const displayPrice = document.getElementById('order-display-price');
            if (displayPrice) displayPrice.textContent = Number(price).toLocaleString() + '원';
        }
    };

    // 3분할 구조: [왼쪽 칸] [가운데 칸] [오른쪽 칸]
    if (type === 'ask') {
        // 매도: [잔량] [가격] [공백]
        row.innerHTML = `
            <span class="hoga-col qty-col">${Number(qty).toLocaleString()}</span>
            <span class="hoga-col price-col">${Number(price).toLocaleString()}</span>
            <span class="hoga-col empty-col"></span>
        `;
    } else {
        // 매수: [공백] [가격] [잔량]
        row.innerHTML = `
            <span class="hoga-col empty-col"></span>
            <span class="hoga-col price-col">${Number(price).toLocaleString()}</span>
            <span class="hoga-col qty-col">${Number(qty).toLocaleString()}</span>
        `;
    }
    return row;
}

// 호가
function subscribeStockHoga(stockCode) {
    // 백엔드와 일치하는 경로: /topic/kis-trade/ask-bid005930 형태
    const topic = '/topic/kis-trade/ask-bid' + stockCode;

    stompClient.subscribe(topic, function(message) {
        const askBidData = JSON.parse(message.body);
        console.log('[호가 실시간 데이터 수신]', askBidData);
        updateHogaUI(askBidData);
    });
}


// ===== API 호출 함수 =====
async function checkBiasAlert(stockCode) {
    console.log('[API 호출] stockCode:', stockCode);
    
    try {
        const url = contextPath + '/api/bias-alert/check?stockCode=' + stockCode;
        console.log('[API URL]', url);
        
        const response = await fetch(url);
        console.log('[API 응답 상태]', response.status);
        
        if (response.status === 204) {
            console.log('[결과] 보유하지 않은 종목');
            return null;
        }
        
        if (!response.ok) {
            throw new Error('API 호출 실패: ' + response.status);
        }
        
        const data = await response.json();
        console.log('[API 결과]', data);
        return data;
        
    } catch (error) {
        console.error('[API 에러]', error);
        return null;
    }
}

// 손실회피 체크 함수
async function checkLossAversionAlert(stockCode) {
    console.log('[손실회피 API 호출] stockCode:', stockCode);
    
    try {
        const url = contextPath + '/api/bias-alert/check-loss-aversion?stockCode=' + stockCode;
        console.log('[손실회피 API URL]', url);
        
        const response = await fetch(url);
        console.log('[손실회피 API 응답 상태]', response.status);
        
        if (response.status === 204) {
            console.log('[결과] 보유하지 않은 종목');
            return null;
        }
        
        if (!response.ok) {
            throw new Error('API 호출 실패: ' + response.status);
        }
        
        const data = await response.json();
        console.log('[손실회피 API 결과]', data);
        return data;
        
    } catch (error) {
        console.error('[손실회피 API 에러]', error);
        return null;
    }
}

// 매몰비용오류 체크 함수
async function checkSunkCostAlert(stockCode) {
    console.log('[매몰비용오류 API 호출] stockCode:', stockCode);
    
    try {
        const url = contextPath + '/api/bias-alert/check-sunk-cost?stockCode=' + stockCode;
        console.log('[매몰비용오류 API URL]', url);
        
        const response = await fetch(url);
        console.log('[매몰비용오류 API 응답 상태]', response.status);
        
        if (response.status === 204) {
            console.log('[결과] 보유하지 않은 종목');
            return null;
        }
        
        if (!response.ok) {
            throw new Error('매몰비용오류 API 호출 실패: ' + response.status);
        }
        
        const data = await response.json();
        console.log('[매몰비용오류 API 결과]', data);
        return data;
        
    } catch (error) {
        console.error('[매몰비용오류 API 에러]', error);
        return null;
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
function drawDetailChart(stockCode) {
    const chartContainer = document.getElementById('detail-stockChart');
    if (!chartContainer) {
        console.error(".detail-chart-area 요소를 찾을 수 없음");
        return;
    }
    
    chartContainer.innerHTML = ''; // 기존 차트가 있다면 삭제
    
    const width = chartContainer.clientWidth;
    const height = chartContainer.clientHeight;

    stockChart = LightweightCharts.createChart(chartContainer, {
        width: width,
        height: height,
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
    fetch(`/antmillion/api/kis/stream/${stockCode}`)
    .then(res => res.json())
    .then(data => {
        if (!data || data.length === 0) {
            console.error("데이터가 비어있음");
            return;
        }

        const currentPrice = document.getElementById("detail-current-price-h3");
        currentPrice.innerText = Number(data[data.length-1].stck_prpr).toLocaleString('ko-KR') + '원';

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

// 날짜 포맷 변경 (main.js의 formatDate 함수와 동일)
function formatDate(yyyymmdd) {
    return {
        year: Number(yyyymmdd.substring(0, 4)),
        month: Number(yyyymmdd.substring(4, 6)),
        day: Number(yyyymmdd.substring(6, 8)),
    };
}

// 일/주/월/년봉 차트 그리기 함수
function drawPeriodChart(stockCode, period) {
    const chartContainer = document.getElementById('detail-stockChart');
    if (!chartContainer) {
        console.error("#detail-stockChart 요소를 찾을 수 없음");
        return;
    }

    chartContainer.innerHTML = ''; // 기존 차트 삭제

    const width = chartContainer.clientWidth;
    const height = chartContainer.clientHeight;

    stockChart = LightweightCharts.createChart(chartContainer, {
        width: width,
        height: height,
        layout: {
            background: {type: 'solid', color: 'white'},
            textColor: 'black'
        },
        grid: {
            vertLines: { color: '#eee' },
            horzLines: { color: '#eee' }
        },
        timeScale: {
            borderColor: '#cccccc',
            timeVisible: false,
            secondsVisible: false
        }
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
    fetch(`/antmillion/api/kis/periodChart/${stockCode}?period=${period}`)
        .then(res => res.json())
        .then(data => {
            if (!data || data.length === 0) {
                console.error("데이터가 비어있음");
                return;
            }

            // 데이터 정렬
            data.sort((a, b) => a.stck_bsop_date.localeCompare(b.stck_bsop_date));

            // 차트 데이터 변환
            const chartData = data.map(row => ({
                time: formatDate(row.stck_bsop_date),
                open: Number(row.stck_oprc),
                high: Number(row.stck_hgpr),
                low: Number(row.stck_lwpr),
                close: Number(row.stck_clpr)
            }));

            if (chartData.length > 0) {
                candleSeries.setData(chartData);
                stockChart.timeScale().fitContent();
            }
        })
        .catch(err => console.error("API 호출 에러:", err));
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const stockCode = urlParams.get('code'); // ?code=005930 에서 005930 추출

    initPriceTypeEvents();
    if (stockCode) {
        drawDetailChart(stockCode);

        // 차트 기간 버튼 이벤트 리스너 추가
        const periodButtons = document.querySelectorAll('.detail-period-btn');
        periodButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                // 모든 버튼에서 active 클래스 제거
                periodButtons.forEach(b => b.classList.remove('detail-period-active'));

                // 클릭된 버튼에 active 클래스 추가
                this.classList.add('detail-period-active');

                const period = this.getAttribute('data-period');

                if (period === 'minute') {
                    // 분봉 차트
                    drawDetailChart(stockCode);
                } else {
                    // 일/주/월/년봉 차트
                    drawPeriodChart(stockCode, period);
                }
            });
        });
    } else {
        console.error("URL에 종목 코드가 없습니다.");
    }
});

// 반응형 대응
window.addEventListener('resize', () => {
    if (stockChart) {
        const container = document.querySelector('#detail-stockChart');
        stockChart.resize(container.clientWidth, container.clientHeight);
    }
});

// ===== DOM 로드 후 실행 =====
document.addEventListener('DOMContentLoaded', async function() {
// 1. URL에서 종목 코드 추출
    const urlParams = new URLSearchParams(window.location.search);
    const stockCode = urlParams.get('code');
    
    if (stockCode) {
        // 전역 변수에 현재 종목 코드를 배열로 저장
        currentStockCodes = [stockCode]; 
        console.log('[실시간 설정] 구독 리스트 등록:', currentStockCodes);

        // 2. STOMP 연결 시도 
        connectStomp();
    }
    
    checkFavoriteStatus();
    console.log('=== 매매 편향 체크 시스템 로드 완료 (API 연동) ===');
    
    // ===== 페이지 진입 시 편향 체크 (우선순위: 매몰비용 > 손실회피) =====
    try {
        const urlParams = new URLSearchParams(window.location.search);
        const stockCode = urlParams.get('code');
        
        if (stockCode) {
            console.log('[페이지 로드] 편향 체크 시작 - stockCode:', stockCode);
            
            // 우선순위 1: 매몰비용오류
            const sunkCostData = await checkSunkCostAlert(stockCode);
            
            // 우선순위 2: 손실회피
            const lossData = await checkLossAversionAlert(stockCode);
            
            // 우선순위에 따라 표시 (매몰비용 > 손실회피)
            if (sunkCostData && sunkCostData.hasAlert) {
                console.log('[우선순위 1] 매몰비용오류 경고: ' + sunkCostData.stockName + ' ' + sunkCostData.profitRate + '% 손실, ' + sunkCostData.holdingDays + '일 보유');
                
                if (typeof showBiasAlert === 'function') {
                    showBiasAlert('SUNK_COST');
                    
                    if (typeof checkUnreadAlerts === 'function') {
                        setTimeout(() => { checkUnreadAlerts(); }, 0);
                    }
                }
            } else if (lossData && lossData.hasAlert) {
                console.log('[우선순위 2] 손실회피 경고: ' + lossData.stockName + ' ' + lossData.profitRate + '% 손실 중');
                
                if (typeof showBiasAlert === 'function') {
                    showBiasAlert('LOSS_AVERSION');
                    
                    if (typeof checkUnreadAlerts === 'function') {
                        setTimeout(() => { checkUnreadAlerts(); }, 0);
                    }
                }
            } else {
                console.log('[페이지 로드] 편향 조건 미달');
            }
        }
    } catch (error) {
        console.error('[페이지 로드] 편향 체크 에러:', error);
    }
    
    // 탭 요소 확인
    const tabs = document.querySelectorAll('.detail-tab, .detail-tab-active');
    console.log('[탭 개수]', tabs.length);
    
    if (tabs.length === 0) {
        console.error('탭을 찾을 수 없습니다!');
        return;
    }
    
    // ===== 탭 전환 기능 =====
    tabs.forEach(function(tab) {
        tab.addEventListener('click', async function() {
            const type = this.dataset.type;
            console.log('[탭 클릭]', type);
            
            if (type === 'pending') { 
                alert('대기 주문 기능은 준비 중입니다.'); 
                return; 
            }
            
            // 모든 탭에서 active 클래스 제거
            document.querySelectorAll('.detail-tab, .detail-tab-active').forEach(function(t) {
                t.classList.remove('detail-tab-active');
                t.classList.add('detail-tab');
            });
            
            // 현재 클릭된 탭에 active 클래스 추가
            this.classList.remove('detail-tab');
            this.classList.add('detail-tab-active');
            
            const btn = document.getElementById('submit-btn');
            const totalMoney = document.getElementById('total-money');
            const availableLabel = document.getElementById('available-label');
            const sentimentDir = document.getElementById('sentiment-direction');
            const sentimentPercent = document.getElementById('sentiment-percent');
            const buyPercent = document.getElementById('buy-percent');
            const sellPercent = document.getElementById('sell-percent');
            const buyBar = document.getElementById('buy-bar');
            const sellBar = document.getElementById('sell-bar');
            
            if (type === 'buy') {
                console.log('[UI 변경] 매수 모드');
                btn.textContent = '매수';
                btn.style.background = '#e22939';
                totalMoney.style.color = '#e22939';
                availableLabel.textContent = '구매 가능 금액';
                sentimentDir.textContent = '매수';
                sentimentDir.className = 'detail-red-text';
                sentimentPercent.textContent = '78';
                buyPercent.textContent = '78%';
                buyPercent.style.width = '78%';
                sellPercent.textContent = '22%';
                sellPercent.style.width = '22%';
                buyBar.style.width = '78%';
                sellBar.style.width = '22%';
                
                // 매수 탭 클릭 시 매몰비용 체크
                const urlParams = new URLSearchParams(window.location.search);
                const stockCode = urlParams.get('code');
                console.log('[종목 코드]', stockCode);
                
                if (stockCode) {
                    try {
                        const sunkCostData = await checkSunkCostAlert(stockCode);
                        
                        if (sunkCostData && sunkCostData.hasAlert) {
                            console.log('[매수 탭] 매몰비용 경고: ' + sunkCostData.stockName + ' ' + sunkCostData.profitRate + '% 손실, ' + sunkCostData.holdingDays + '일 보유');
                            
                            if (typeof showBiasAlert === 'function') {
                                showBiasAlert('SUNK_COST');
                                
                                if (typeof checkUnreadAlerts === 'function') {
                                    setTimeout(() => { checkUnreadAlerts(); }, 0);
                                }
                            }
                        } else {
                            console.log('[매수 탭] 매몰비용 조건 미달');
                        }
                    } catch (error) {
                        console.error('[매수 탭] 매몰비용 체크 에러:', error);
                    }
                } else {
                    console.warn('URL에 종목 코드(code) 없음');
                }
                   
            } else if (type === 'sell') {
                console.log('[UI 변경] 매도 모드');
                btn.textContent = '매도';
                btn.style.background = '#2271e9';
                totalMoney.style.color = '#2271e9';
                availableLabel.textContent = '판매 가능 수량';
                sentimentDir.textContent = '매도';
                sentimentDir.className = 'detail-blue-text';
                sentimentPercent.textContent = '22';
                buyPercent.textContent = '22%';
                buyPercent.style.width = '22%';
                sellPercent.textContent = '78%';
                sellPercent.style.width = '78%';
                buyBar.style.width = '22%';
                sellBar.style.width = '78%';
                
                // 매도 탭 클릭 시 위험회피 체크
                const urlParams2 = new URLSearchParams(window.location.search);
                const stockCode2 = urlParams2.get('code');
                console.log('[종목 코드]', stockCode2);
                
                if (stockCode2) {
                    try {
                        const riskData = await checkBiasAlert(stockCode2);
                        
                        if (riskData && riskData.hasAlert) {
                            console.log('위험회피 경고: ' + riskData.stockName + ' +' + riskData.profitRate + '% 수익 중 (' + riskData.holdingDays + '일 보유)');
                            
                            if (typeof showBiasAlert === 'function') {
                                showBiasAlert('RISK_AVERSION');
                                
                                if (typeof checkUnreadAlerts === 'function') {
                                    setTimeout(() => { checkUnreadAlerts(); }, 0);
                                }
                            }
                        } else {
                            // 위험회피 조건 미달
                            if (riskData) {
                                console.log('위험회피 조건 미달 - 수익률: ' + riskData.profitRate + '%, 보유일수: ' + riskData.holdingDays + '일');
                            }
                            // 손실회피 경고는 유지하므로 hideBiasAlert() 호출 안 함
                            console.log('[경고] 기존 경고 유지 (손실회피 경고가 있을 수 있음)');
                        }
                    } catch (error) {
                        console.error('[매도 탭] 위험회피 체크 에러:', error);
                    }
                } else {
                    console.warn('URL에 종목 코드(code) 없음');
                }
            }
        });
    });
});

// 페이지 로드 시 관심종목 상태 확인
function checkFavoriteStatus() {
    const stockCode = document.querySelector('.detail-favorite-btn').getAttribute('data-code');

    fetch(contextPath + '/api/interest/list')
        .then(res => res.json())
        .then(interestCodes => {
            const btn = document.querySelector('.detail-favorite-btn');
            const isFavorite = interestCodes.includes(stockCode);

            btn.textContent = isFavorite ? '♥' : '♡';
            if (isFavorite) {
                btn.classList.add('active');
            }
        });
}

// 관심종목 토글 버튼 이벤트
document.querySelector('.detail-favorite-btn').addEventListener('click', function() {
    const stockCode = this.getAttribute('data-code');

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
                this.classList.toggle('active');
                this.textContent = data.isInterest ? '♥' : '♡';
            }
        })
        .catch(error => {
            console.error('관심종목 토글 실패:', error);
        });
});

// ========== Mock 데이터 제어 함수 ==========

let isMockMode = false; // Mock 모드 플래그

// Mock 모드로 전환 (한투 구독 건너뛰고 Mock만 사용)
function enableMockMode() {
    isMockMode = true;
    console.log('Mock 모드 활성화');

    // 현재 화면에 표시된 종목 리스트 가져오기
    const stockItems = document.querySelectorAll('.stocklist-item');
    const mockStocks = [];

    stockItems.forEach(item => {
        const stockCode = item.getAttribute('data-code');
        const priceText = item.querySelector('.stocklist-price').textContent;
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

// Mock 상태 확인
function checkMockStatus() {
    fetch(contextPath + '/api/kis/mock/status')
        .then(res => res.json())
        .then(response => {
            console.log('Mock 상태:', response.isRunning ? '실행 중' : '중지됨');
            console.log('Mock 종목 수:', response.stockCount);
            alert('테스트 데이터 상태: ' + (response.isRunning ? '실행 중' : '중지됨') + '\n종목 수: ' + response.stockCount);
        })
        .catch(error => {
            console.error('Mock 상태 확인 실패:', error);
        });
}

// 브라우저 콘솔에서 사용 가능하도록 전역으로 노출
window.enableMockMode = enableMockMode;
window.disableMockMode = disableMockMode;
window.checkMockStatus = checkMockStatus;