// ===========================
// STOMP 웹소켓 관련 변수 (추가)
// ===========================
let stompClient = null;
let subscribedStockTopics = {}; // 구독한 종목 토픽 저장 {stockCode: subscription}
let currentHoldingStocks = []; // 현재 보유 중인 종목 정보 (code 포함)

// 계좌 잔고
let accountBalance = 0;

// ===========================
// STOMP 연결
// ===========================
function connectStompForStockHoldings() {
    const url = contextPath + '/ws-stomp';
    const socket = new SockJS(url);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('마이페이지 STOMP 연결 성공: ' + frame);

        // 연결 성공 후 보유 종목들 구독
        if (currentHoldingStocks.length > 0) {
            subscribeHoldingStocks();
        }
    }, function(error) {
        console.error('마이페이지 STOMP 연결 실패: ' + error);
        // 5초 후 재연결 시도
        setTimeout(connectStompForStockHoldings, 5000);
    });
}

// ===========================
// 보유 종목 백엔드 구독 및 STOMP 토픽 구독
// ===========================
function subscribeHoldingStocks() {
    const stockCodes = currentHoldingStocks.map(stock => stock.code);

    if (stockCodes.length === 0) return;

    // 1. 백엔드에 한국투자증권 구독 요청
    fetch(contextPath + '/api/kis/websocket/subscribe-multiple?trId=H0STCNT0', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(stockCodes)
    })
        .then(res => res.json())
        .then(response => {
            console.log('마이페이지 백엔드 구독 완료:', response);

            // 2. 프론트엔드 STOMP 토픽 구독
            stockCodes.forEach(stockCode => {
                subscribeStockTopicForHolding(stockCode);
            });
        })
        .catch(error => {
            console.error('마이페이지 백엔드 구독 실패:', error);
        });
}

// ===========================
// 개별 종목 STOMP 토픽 구독
// ===========================
function subscribeStockTopicForHolding(stockCode) {
    if (subscribedStockTopics[stockCode]) {
        console.log('이미 구독 중:', stockCode);
        return;
    }

    const topic = '/topic/kis-trade/present' + stockCode;
    const subscription = stompClient.subscribe(topic, function(message) {
        const tradeData = JSON.parse(message.body);
        console.log('마이페이지 실시간 데이터 수신:', tradeData);

        // 화면 업데이트
        updateStockHoldingRealtime(stockCode, tradeData);
    });

    subscribedStockTopics[stockCode] = subscription;
    console.log('마이페이지 토픽 구독 완료:', topic);
}

// ===========================
// 실시간 현재가로 주식 잔고 카드 업데이트
// ===========================
function updateStockHoldingRealtime(stockCode, tradeData) {
    // 해당 종목의 DOM 요소 찾기
    const stockCard = document.querySelector(`.mypage-stock-card[data-code="${stockCode}"]`);
    if (!stockCard) return;

    // 현재가 업데이트
    const currentPrice = Number(tradeData.stckPrpr);
    const currentPriceElement = stockCard.querySelector('.stock-current-price');
    if (currentPriceElement) {
        currentPriceElement.textContent = currentPrice.toLocaleString() + '원';
    }

    // 해당 종목의 보유 정보 찾기
    const holdingStock = currentHoldingStocks.find(stock => stock.code === stockCode);
    if (!holdingStock) return;

    const shares = holdingStock.shares;
    const buyPrice = holdingStock.buyPrice;

    // 평가금액 = 현재가 × 보유수량
    const totalValue = currentPrice * shares;

    // 수익금액 = 평가금액 - 매수금액
    const profit = totalValue - buyPrice;

    // 수익률 = (수익금액 / 매수금액) × 100
    const profitRate = ((profit / buyPrice) * 100).toFixed(2);

    // currentHoldingStocks 배열의 해당 종목 데이터도 업데이트
    holdingStock.currentPrice = currentPrice;
    holdingStock.totalValue = totalValue;
    holdingStock.profit = profit;
    holdingStock.profitRate = profitRate;

    // 평가금액 업데이트
    const totalValueElement = stockCard.querySelector('.stock-total-value');
    if (totalValueElement) {
        totalValueElement.textContent = totalValue.toLocaleString() + '원';
    }

    // 수익금액 및 수익률 업데이트
    const profitElement = stockCard.querySelector('.mypage-stock-profit');
    if (profitElement) {
        const profitClass = profit > 0 ? 'positive' : profit < 0 ? 'negative' : 'neutral';
        const profitSign = profit > 0 ? '+' : '';

        profitElement.className = `mypage-stock-profit ${profitClass}`;
        profitElement.textContent = `${profitSign}${profit.toLocaleString()}원(${profitSign}${profitRate}%)`;
    }

    updateTotalAsset();
}

// ===========================
// 모든 구독 해제
// ===========================
function unsubscribeAllHoldingStocks() {
    console.log('마이페이지 구독 해제 시작...');

    // 프론트엔드 STOMP 구독 해제
    for (let stockCode in subscribedStockTopics) {
        if (subscribedStockTopics[stockCode]) {
            subscribedStockTopics[stockCode].unsubscribe();
            console.log('토픽 구독 해제:', stockCode);
        }
    }
    subscribedStockTopics = {};

    // 백엔드 구독 해제
    if (currentHoldingStocks.length > 0) {
        fetch(contextPath + '/api/kis/websocket/unsubscribe-all?trId=H0STCNT0', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            keepalive: true
        })
            .then(res => res.json())
            .then(response => {
                console.log('마이페이지 백엔드 구독 해제 완료:', response);
            })
            .catch(error => {
                console.error('마이페이지 백엔드 구독 해제 실패:', error);
            });
    }
}

// ===========================
// BiasType 매핑
// ===========================
const biasTypeMap = {
    RISK_AVERSION: { title: "위험회피 주의", icon: "⚠️" },
    LOSS_AVERSION: { title: "손실회피 주의", icon: "⚠️" },
    SUNK_COST: { title: "매몰비용 경고", icon: "⚠️" },
    FOMO: { title: "FOMO 주의", icon: "⚠️" }
};

// ===========================
// 샘플 데이터
// ===========================
const sampleData = {
    // 실현손익 데이터
    realizedProfits: [
        {
            date: '2025.12',
            amount: 211048,
            items: [
                {
                    date: '12.03',
                    stock: '두산에너빌리티',
                    amount: 43012
                },
                {
                    date: '12.01',
                    stock: '삼성전자',
                    amount: 168036
                }
            ]
        },
        {
            date: '2025.11',
            amount: -211048,
            items: [
                {
                    date: '11.03',
                    stock: '두산에너빌리티',
                    amount: -43012
                },
                {
                    date: '11.01',
                    stock: '삼성전자',
                    amount: -168036
                }
            ]
        },
        {
            date: '2025.10',
            amount: 211048,
            items: [
                {
                    date: '10.03',
                    stock: '두산에너빌리티',
                    amount: 43012
                },
                {
                    date: '10.01',
                    stock: '삼성전자',
                    amount: 168036
                },
                {
                    date: '10.03',
                    stock: '두산에너빌리티',
                    amount: 43012
                },
                {
                    date: '10.03',
                    stock: '두산에너빌리티',
                    amount: 43012
                },
                {
                    date: '10.03',
                    stock: '두산에너빌리티',
                    amount: 43012
                }
            ]
        }
    ],
    
    // 체결내역 데이터
    executedOrders: {
        all: [
            {
                stock: '신한지주',
                type: 'sell',
                orderQty: 20,
                executedQty: 20,
                unexecutedQty: 0,
                unexecutedAmount: 0,
                time: '11:20:11',
                orderPrice: 77000,
                executedPrice: 77000,
                orderAmount: 0,
                stockCode: '055550',
                executedTime: '11:20:11'
            },
            {
                stock: '신한지주',
                type: 'sell',
                orderQty: 20,
                executedQty: 20,
                unexecutedQty: 0,
                unexecutedAmount: 0,
                time: '11:20:11',
                orderPrice: 77000,
                executedPrice: 77000,
                orderAmount: 0,
                stockCode: '055550',
                executedTime: '11:20:11'
            },
            {
                stock: '신한지주',
                type: 'buy',
                orderQty: 20,
                executedQty: 20,
                unexecutedQty: 0,
                unexecutedAmount: 0,
                time: '11:20:11',
                orderPrice: 77000,
                executedPrice: 77000,
                orderAmount: 0,
                stockCode: '055550',
                executedTime: '11:20:11'
            },
            {
                stock: '삼성전자',
                type: 'buy',
                orderQty: 10,
                executedQty: 10,
                unexecutedQty: 0,
                unexecutedAmount: 0,
                time: '10:54:01',
                orderPrice: 77000,
                executedPrice: 77000,
                orderAmount: 0,
                stockCode: '005930',
                executedTime: '10:54:01'
            },
            {
                stock: 'SK하이닉스',
                type: 'buy',
                orderQty: 1,
                executedQty: 0,
                unexecutedQty: 1,
                unexecutedAmount: 742000,
                time: '11:20:11',
                orderPrice: 742000,
                executedPrice: '-',
                orderAmount: 742000,
                stockCode: '000660',
                executedTime: '-'
            }
        ]
    },
    
    // 매매내역 데이터
    tradingHistory: [
        {
            date: '2026.01.02',
            stock: '삼성전자',
            type: 'sell',
            shares: 10,
            amount: 1017000,
            detail: '77,000원'
        },
        {
            date: '2026.01.02',
            stock: '삼성전자',
            type: 'sell',
            shares: 10,
            amount: 1017000,
            detail: '77,000원'
        },
        {
            date: '2026.01.02',
            stock: '삼성전자',
            type: 'buy',
            shares: 10,
            amount: 1017000,
            detail: '77,000원'
        },
        {
            date: '2026.01.02',
            stock: '삼성전자',
            type: 'buy',
            shares: 10,
            amount: 1017000,
            detail: '77,000원'
        },
        {
            date: '2026.01.02',
            stock: '삼성전자',
            type: 'buy',
            shares: 10,
            amount: 1017000,
            detail: '77,000원'
        },
        {
            date: '2026.01.02',
            stock: '신한지주',
            type: 'buy',
            shares: 20,
            amount: 1540000,
            detail: '77,000원'
        }
    ]
};

// ===========================
// DOM 요소
// ===========================
let stockList, realizedList, executedTable, tradingList;
let tabButtons, subTabButtons, filterButtons;

// ===========================
// 초기화
// ===========================
document.addEventListener('DOMContentLoaded', function() {
    console.log("마이페이지 로드 완료");
    
    // 1. UI 요소 참조 및 기본 이벤트 연결 (이게 최우선)
    initializeElements();
    setupEventListeners(); 
    
    // 2. 첫 화면 데이터만 로드 (주식 잔고)
    loadStockHoldings();
    
    // 탭 기간 조회 초기화
    initializeSelectDates();
    
    // 3. 심리경고 전용 로직
    initializeHistoryDates();
    loadHistoryData();
    setupHistoryEventListeners();
    
    // ※ loadRealizedProfit()은 여기서 직접 호출하지 마세요!
});

// ===========================
// 이벤트 리스너 통합 설정
// ===========================
function setupEventListeners() {
    // 메인 탭 전환 버튼 이벤트
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const target = this.dataset.tab; // 'stock', 'realized' 등
            console.log("탭 전환:", target);
            
            // 1. 모든 버튼 및 패널 비활성화
            tabButtons.forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.mypage-tab-panel').forEach(panel => {
                panel.classList.remove('active');
            });
            
            // 2. 현재 클릭한 탭 활성화
            this.classList.add('active');
            const targetPanel = document.getElementById(`${target}-panel`);
            if (targetPanel) targetPanel.classList.add('active');
            
            // 3. 탭별 데이터 로드
            if (target === 'stock') {
                loadStockHoldings();
            } else if (target === 'realized') {
                loadRealizedProfit(); 
            } else if (target === 'account') { // 👈 계좌정보 탭 추가
                loadAccountInfo();
            }
        });
    });

    // 날짜 변경 시 자동 조회 (실현손익용)
    const realizedDates = [document.getElementById("realizedStartDate"), document.getElementById("realizedEndDate")];
    realizedDates.forEach(input => {
        if(input) {
            input.addEventListener("change", () => loadRealizedProfit());
        }
    });

    // 기존의 나머지 리스너들 (서브탭 등) 유지...
    setupDateFilters(); 
}

function initializeElements() {
    // DOM 요소 가져오기
    stockList = document.getElementById('stockList');
    realizedList = document.getElementById('realizedList');
    executedTable = document.getElementById('executedTable');
    tradingList = document.getElementById('tradingList');
    
    // 버튼 요소
    tabButtons = document.querySelectorAll('.mypage-tab-button');
    subTabButtons = document.querySelectorAll('.mypage-sub-tab-button');
    filterButtons = document.querySelectorAll('.mypage-filter-button');
}



// ===========================
// 기간 선택 필터 설정
// ===========================
function setupDateFilters() {
    // 날짜 입력 필드들
    const dateInputs = document.querySelectorAll('.mypage-date-input');
    const clearButtons = document.querySelectorAll('.mypage-date-clear');
    
    // 오늘 날짜로 초기화
    const today = new Date().toISOString().split('T')[0];
    dateInputs.forEach(input => {
        if (!input.value) {
            input.value = today;
        }
    });
    
    // 클리어 버튼 이벤트
    clearButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('data-target');
            const targetInput = document.getElementById(targetId);
            if (targetInput) {
                targetInput.value = '';
            }
        });
    });
    
    // 날짜 변경 이벤트
    dateInputs.forEach(input => {
        input.addEventListener('change', function() {
            console.log(`Date changed: ${this.id} = ${this.value}`);
        });
    });
}

// ===========================
// 탭 전환
// ===========================
function switchTab(tabName) {
    // 모든 탭 버튼 비활성화
    tabButtons.forEach(btn => btn.classList.remove('active'));
    
    // 클릭된 탭 버튼 활성화
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    
    // 모든 탭 패널 숨기기
    document.querySelectorAll('.mypage-tab-panel').forEach(panel => {
        panel.classList.remove('active');
    });
    
    // 선택된 탭 패널 표시
    const panelId = `${tabName}-panel`;
    document.getElementById(panelId).classList.add('active');
}

function switchSubTab(subtab) {
    // 모든 서브 탭 버튼 비활성화
    subTabButtons.forEach(btn => btn.classList.remove('active'));
    
    // 클릭된 서브 탭 버튼 활성화
    document.querySelector(`[data-subtab="${subtab}"]`).classList.add('active');
    
    // 체결내역 데이터 필터링 및 렌더링
    renderExecutedOrders(subtab);
}

function filterTradingHistory(filter) {
    // 모든 필터 버튼 비활성화
    filterButtons.forEach(btn => btn.classList.remove('active'));
    
    // 클릭된 필터 버튼 활성화
    document.querySelector(`[data-filter="${filter}"]`).classList.add('active');
    
    // 매매내역 데이터 필터링 및 렌더링
    renderTradingHistory(filter);
}

// ===========================
// 초기 데이터 렌더링
// ===========================
function renderInitialData() {
    //renderStockHoldings();
    loadStockHoldings(); // 
    renderRealizedProfits();
    renderExecutedOrders('all');
    renderTradingHistory('all');
}

// DB에서 주식 잔고 데이터를 가져오는 함수 (수정)
function loadStockHoldings() {
    // 1. 계좌 정보 조회 (잔고 가져오기)
    fetch(contextPath + '/mypage/api/account-info')
        .then(res => res.json())
        .then(accountData => {
            // 계좌 잔고 저장
            accountBalance = accountData.balance || 0;
            console.log('계좌 잔고:', accountBalance);

            // 2. 주식 잔고 조회
            return fetch(contextPath + '/mypage/api/stock-holdings');
        })
        .then(res => res.json())
        .then(data => {
            if (!data || data.length === 0) {
                if (stockList) stockList.innerHTML = "<p class='no-data'>보유 중인 주식이 없습니다.</p>";
                // 잔고만 있는 경우에도 총 자산 업데이트
                currentHoldingStocks = [];
                updateTotalAsset();
                return;
            }

            // 현재 보유 종목 정보 저장 (code 포함)
            currentHoldingStocks = data;

            // 화면 렌더링
            renderStockHoldings(data);

            // 추가: 초기 총 자산 업데이트
            updateTotalAsset();

            // STOMP 연결 및 구독
            if (stompClient && stompClient.connected) {
                subscribeHoldingStocks();
            } else {
                connectStompForStockHoldings();
            }
        })
        .catch(err => {
            console.error("주식 잔고 로드 실패:", err);
            if (stockList) stockList.innerHTML = "<p class='no-data'>데이터 로드 실패</p>";
        });
}

// ===========================
// 총 자산 계산 및 업데이트
// ===========================
function updateTotalAsset() {
    // 1. 보유 주식의 총 평가금액, 총 매수금액, 총 수익 계산
    let totalStockValue = 0;  // 보유 주식 총 평가금액
    let totalBuyPrice = 0;     // 총 매수금액
    let totalProfit = 0;       // 총 수익금액

    currentHoldingStocks.forEach(stock => {
        totalStockValue += (stock.totalValue || 0);
        totalBuyPrice += (stock.buyPrice || 0);
        totalProfit += (stock.profit || 0);
    });

    // 2. 내 자산 총액 = 계좌 잔고 + 보유 주식 총 평가금액
    const totalAsset = accountBalance + totalStockValue;

    // 3. 총 평가손익률 = (총 평가손익 / 총 매수금액) × 100
    const totalProfitRate = totalBuyPrice > 0
        ? ((totalProfit / totalBuyPrice) * 100).toFixed(2)
        : '0.00';

    // 4. DOM 업데이트 - 내 자산 총액
    const totalAssetElement = document.getElementById('totalAsset');
    if (totalAssetElement) {
        totalAssetElement.textContent = totalAsset.toLocaleString() + '원';
    }

    // 5. DOM 업데이트 - 총 평가 손익
    const totalProfitElement = document.getElementById('totalProfit');
    if (totalProfitElement) {
        const profitClass = totalProfit > 0 ? 'positive' : totalProfit < 0 ? 'negative' : 'neutral';
        const profitSign = totalProfit > 0 ? '+' : '';

        // 기존 클래스를 유지하면서 positive/negative만 변경
        totalProfitElement.className = `mypage-profit-amount ${profitClass}`;
        totalProfitElement.textContent = `${profitSign}${totalProfit.toLocaleString()}원(${profitSign}${totalProfitRate}%)`;
    }
}

// ===========================
// 주식잔고 렌더링 (수정)
// ===========================
function renderStockHoldings(holdings) { // 매개변수 추가
    if (!stockList) return;
    
    // 데이터가 없을 때 처리 추가
    if (!holdings || holdings.length === 0) {
        stockList.innerHTML = "<p class='no-data'>보유 중인 주식이 없습니다.</p>";
        return;
    }
    
    // sampleData.stockHoldings 대신 holdings 사용
    stockList.innerHTML = holdings.map(stock => {
        const profitClass = stock.profit > 0 ? 'positive' : stock.profit < 0 ? 'negative' : 'neutral';
        const profitSign = stock.profit > 0 ? '+' : '';
        
        return `
            <div class="mypage-stock-card" data-code="${stock.code}">
                <div class="mypage-stock-header">
                    <div>
                        <div class="mypage-stock-name">${stock.name}</div>
                        <div class="mypage-stock-shares">현금 ${stock.shares}주</div>
                    </div>
                    <div class="mypage-stock-profit ${profitClass}">
                        ${profitSign}${stock.profit.toLocaleString()}원(${profitSign}${stock.profitRate ? stock.profitRate : '0.00'}%)
                    </div>
                </div>
                <div class="mypage-stock-details">
                    <div class="mypage-stock-detail-item">
                        <span class="mypage-detail-label">매수 금액</span>
                        <span class="mypage-detail-value">${stock.buyPrice.toLocaleString()}원</span>
                    </div>
                    <div class="mypage-stock-detail-item">
                        <span class="mypage-detail-label">평균단가</span>
                        <span class="mypage-detail-value">${stock.avgBuyPrice.toLocaleString()}원</span>
                    </div>
                    <div class="mypage-stock-detail-item">
                        <span class="mypage-detail-label">평가 금액</span>
                        <span class="mypage-detail-value stock-total-value">${stock.totalValue.toLocaleString()}원</span>
                    </div>
                    <div class="mypage-stock-detail-item">
                        <span class="mypage-detail-label">현재가</span>
                        <span class="mypage-detail-value stock-current-price">${stock.currentPrice.toLocaleString()}원</span>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// 실현손익 데이터를 불러오는 함수
function loadRealizedProfit() {
    // 1. JSP input에서 날짜 값 가져오기
    const startDate = document.getElementById("realizedStartDate").value;
    const endDate = document.getElementById("realizedEndDate").value;

    console.log("조회 기간:", startDate, "~", endDate);

    // 2. URL에 파라미터 추가
    const url = `${contextPath}/mypage/api/realized-profit?startDate=${startDate}&endDate=${endDate}`;

    fetch(url)
        .then(res => res.json())
        .then(data => {
            renderRealizedProfit(data);
        })
        .catch(err => console.error("실현손익 조회 실패:", err));
}

// ===========================
// 실현손익 렌더링
// ===========================
function renderRealizedProfit(data) {
    const listContainer = document.querySelector("#realizedList");
    if (!listContainer) return;

    if (!data || data.length === 0) {
        listContainer.innerHTML = "<p class='no-data'>해당 기간 내 실현손익 내역이 없습니다.</p>";
        return;
    }

    // 1. 데이터 월별 그룹화 처리
    const groupedData = {};

    data.forEach(item => {
        let dateObj;
        if (Array.isArray(item.tradeDate)) {
            // [2026, 1, 22, ...] 형태 처리
            dateObj = new Date(item.tradeDate[0], item.tradeDate[1] - 1, item.tradeDate[2]);
        } else {
            dateObj = new Date(item.tradeDate);
        }

        const monthKey = `${dateObj.getFullYear()}년 ${dateObj.getMonth() + 1}월`;
        
        if (!groupedData[monthKey]) {
            groupedData[monthKey] = {
                month: monthKey,
                totalProfit: 0,
                items: []
            };
        }
        
        groupedData[monthKey].items.push({
            ...item,
            formattedDate: `${dateObj.getMonth() + 1}월 ${dateObj.getDate()}일`
        });
        groupedData[monthKey].totalProfit += item.profit;
    });

    // 2. HTML 생성 (기존 UI 디자인 적용)
    let html = "";
    
    // 월별로 반복
    Object.values(groupedData).sort((a, b) => b.month.localeCompare(a.month)).forEach(group => {
        const isMonthProfitable = group.totalProfit >= 0;
        const monthClass = isMonthProfitable ? 'positive' : 'negative';
        const monthSign = isMonthProfitable ? '+' : '';

        html += `
            <div class="mypage-realized-item">
                <div class="mypage-realized-header">
                    <span class="mypage-realized-date">${group.month} </span>
                    <span class="mypage-realized-amount ${monthClass}">
                        ${monthSign}${formatNumber(group.totalProfit)}원
                    </span>
                </div>
                
                <div class="mypage-realized-detail">
                    ${group.items.map(item => {
                        const isItemProfitable = item.profit >= 0;
                        const itemClass = isItemProfitable ? 'positive' : 'negative';
                        const itemSign = isItemProfitable ? '+' : '';
                        
                        
                        return `
                            <div style="margin-top: 15px; padding-top: 15px; border-top: 1px dashed var(--border-color);">
                                <div class="mypage-realized-stock" style="font-weight: bold; font-size: 15px; margin-bottom: 5px;">
                                    ${item.name}
                                </div>
                                <div class="mypage-realized-info" style="display: flex; justify-content: space-between; font-size: 13px;">
                                    <span>${item.formattedDate} · ${item.shares}주 매도</span>
                                    <span class="${itemClass}" style="font-weight: bold;">
                                        ${itemSign}${formatNumber(item.profit)}원 (${itemSign}${Number(item.profitRate).toFixed(2)}%)
                                    </span>
                                </div>
                                <div style="display: flex; gap: 10px; font-size: 11px; color: var(--text-secondary); margin-top: 4px;">
                                    <span>매수가: ${formatNumber(item.buyPrice)}원</span>
                                    <span>매도가: ${formatNumber(item.sellPrice)}원</span>
                                </div>
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `;
    });

    listContainer.innerHTML = html;
}


// ===========================
// 체결내역 렌더링
// ===========================
function renderExecutedOrders(filter) {
    if (!executedTable) return;
    
    const tbody = executedTable.querySelector('tbody');
    if (!tbody) return;
    
    let orders = sampleData.executedOrders.all;
    
    // 필터 적용
    if (filter === 'executed') {
        orders = orders.filter(order => order.executedQty > 0 && order.unexecutedQty === 0);
    } else if (filter === 'unexecuted') {
        orders = orders.filter(order => order.unexecutedQty > 0);
    }
    
    tbody.innerHTML = orders.map(order => {
        const typeClass = order.type === 'buy' ? 'mypage-buy-badge' : 'mypage-sell-badge';
        const typeText = order.type === 'buy' ? '매수' : '매도';
        
        return `
            <tr class="mypage-main-row">
                <td>${order.stock}</td>
                <td>${order.orderQty}</td>
                <td>${order.executedQty}</td>
                <td>${order.unexecutedQty}</td>
                <td>${order.unexecutedAmount === 0 ? '0' : order.unexecutedAmount.toLocaleString()}</td>
                <td>${order.time}</td>
            </tr>
            <tr class="mypage-sub-row">
                <td><span class="${typeClass}">${typeText}</span></td>
                <td>${order.orderPrice === '-' ? '-' : order.orderPrice.toLocaleString()}</td>
                <td>${order.executedPrice === '-' ? '-' : order.executedPrice.toLocaleString()}</td>
                <td>${order.orderAmount === 0 ? '0' : order.orderAmount.toLocaleString()}</td>
                <td>${order.stockCode}</td>
                <td>${order.executedTime}</td>
            </tr>
        `;
    }).join('');
}

/**
 * 계좌 정보 데이터를 서버에서 가져와 화면에 렌더링
 */
/**
 * 계좌 정보 데이터를 서버에서 가져와 화면에 렌더링
 */
function loadAccountInfo() {
    fetch(`${contextPath}/mypage/api/account-info`)
        .then(response => {
            if (!response.ok) throw new Error("계좌 정보 로드 실패");
            return response.json();
        })
        .then(data => {
            if (data) {
                document.getElementById("userNickname").innerText = data.nickname;
                document.getElementById("accNumber").innerText = data.accountNumber || "정보 없음";
                document.getElementById("accBalance").innerText = formatNumber(data.balance || 0);
                
                // ✅ 날짜 처리 로직 개선
                const createDateEl = document.getElementById("accCreateDate");
                if (data.createdAt) {
                    // 서버 데이터가 [2026, 1, 22] 배열 형식이거나 문자열인 경우 모두 대응
                    const date = Array.isArray(data.createdAt) 
                        ? new Date(data.createdAt[0], data.createdAt[1] - 1, data.createdAt[2])
                        : new Date(data.createdAt);

                    if (!isNaN(date.getTime())) {
                        createDateEl.innerText = date.toLocaleDateString('ko-KR');
                    } else {
                        createDateEl.innerText = data.createdAt; // 변환 실패 시 원본 표시
                    }
                }
            }
        })
        .catch(error => console.error("Error:", error));
}


/**
 * 기간 날짜 필드 초기화 (한달전 ~ 오늘 날짜로)
 */
function initializeSelectDates() {
    const now = new Date(); // 현재 날짜 객체 생성
    const today = now.toISOString().split('T')[0];
    
    // 한 달 전 날짜 계산
    const lastMonth = new Date(now.setMonth(now.getMonth() - 1)).toISOString().split('T')[0];
    
    const startDateInput = document.getElementById("realizedStartDate");
    const endDateInput = document.getElementById("realizedEndDate");
    
    if (startDateInput) {
        // 오늘(today) 대신 한 달 전(lastMonth) 값을 할당
        startDateInput.value = lastMonth;
        console.log("기간 시작 날짜 초기화:", lastMonth);
    }
    
    if (endDateInput) {
        endDateInput.value = today;
        console.log("기간 종료 날짜 초기화:", today);
    }
}

/**
 * 심리경고 날짜 필드 초기화 (오늘 날짜로)
 */
function initializeHistoryDates() {
    const today = new Date().toISOString().split('T')[0];
    
    const startDateInput = document.getElementById("alertStartDate");
    const endDateInput = document.getElementById("alertEndDate");
    
    if (startDateInput) {
        startDateInput.value = today;
        console.log("심리경고 시작 날짜 초기화:", today);
    }
    
    if (endDateInput) {
        endDateInput.value = today;
        console.log("심리경고 종료 날짜 초기화:", today);
    }
}

/**
 * 심리경고 데이터 로드
 */
function loadHistoryData() {
    const alertList = document.querySelector(".mypage-alert-list");
    if (!alertList) {
        console.error("심리경고 목록 요소를 찾을 수 없습니다.");
        return;
    }

    const startDateInput = document.getElementById("alertStartDate");
    const endDateInput = document.getElementById("alertEndDate");
    
    if (!startDateInput || !endDateInput) {
        console.error("날짜 입력 필드를 찾을 수 없습니다.");
        return;
    }

    const startDate = startDateInput.value;
    const endDate = endDateInput.value;

    console.log("심리경고 조회 - startDate:", startDate, "endDate:", endDate);

    let url = "/antmillion/api/history/list";

    if (startDate && endDate) {
        url += `?startDate=${startDate}&endDate=${endDate}`;
    }

    console.log("요청 URL:", url);

    fetch(url)
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            console.log("받은 데이터:", data);
            
            if (!data || data.length === 0) {
                alertList.innerHTML = "<p class='no-data'>해당 기간의 심리경고가 없습니다.</p>";
                return;
            }

            alertList.innerHTML = "";

            data.forEach(history => {
                const date = new Date(history.time);
                const formattedDate = 
                    date.getFullYear() + "/" +
                    String(date.getMonth() + 1).padStart(2, '0') + "/" +
                    String(date.getDate()).padStart(2, '0') + " " +
                    String(date.getHours()).padStart(2, '0') + ":" +
                    String(date.getMinutes()).padStart(2, '0') + ":" +
                    String(date.getSeconds()).padStart(2, '0');

                const biasInfo = biasTypeMap[history.biasType] || {
                    title: "심리 편향 경고",
                    icon: "⚠️"
                };

                const transactionClass = history.transactionType === '매수' ? 'buy' : 'sell';
                const transactionText = history.transactionType || '-';

                // 수정된 HTML 구조
                const html = `
                    <div class="mypage-warning-container" data-stock-code="${history.stockCode}" data-bias-type="${history.biasType}" data-time="${history.time}">
                        <div class="mypage-warning-header">
                            <span class="warning-icon">⚠️</span>
                            <span class="warning-title">${biasInfo.title}</span>
                            <span class="info-icon" data-bias-type="${history.biasType}">ⓘ</span>
                        </div>

                        <div class="inner-trade-card">
                            <div class="trade-info-top">
                                <span class="trade-label">매매내역</span>
                                <span class="trade-date">${formattedDate}</span>
                            </div>
                            <div class="trade-info-main">
                                <span class="stock-name">${history.stockName || "-"}</span>
                                <span class="trade-amount">
                                    ${history.totalAmount ? history.totalAmount.toLocaleString() : "0"}원
                                </span>
                            </div>
                            <div class="trade-info-bottom">
                                <span class="trade-type ${transactionClass}">${transactionText}</span>
                                <span class="trade-quantity">${history.quantity || 0}주</span>
                                <span class="unit-price">
                                    ${history.orderPrice ? history.orderPrice.toLocaleString() : "0"}원
                                </span>
                            </div>
                        </div>

                        <p class="mypage-alert-description">
                            ${history.messageDetail || ""}
                        </p>
                    </div>
                `;
                
                alertList.insertAdjacentHTML("beforeend", html);
            });
            
            console.log("렌더링 완료");
            
            // 툴팁 설정
            setupBiasTooltips();
        })
        .catch(error => {
            console.error("심리경고 데이터 로드 실패:", error);
            alertList.innerHTML = "<p class='no-data'>심리경고 데이터를 불러오지 못했습니다.</p>";
        });
}

/**
 * 편향 설명 툴팁 설정
 */
function setupBiasTooltips() {
    const biasDescriptions = {
        'RISK_AVERSION': '위험회피란? 전망이론에 따르면 투자자들은 이익 영역에서 확실한 작은 이익을 선호하는 경향이 있습니다.\n 주의: 성급한 매도를 경계하세요',
        
        'LOSS_AVERSION': '손실회피란? 투자자들은 이익보다 손실을 약 2.25배 더 크게 느끼며, 손실을 확정짓지 않으려는 경향이 있습니다.\n 주의: 손실 확정을 미루고 있지 않은지 확인하세요',
        
        'SUNK_COST': '매몰비용오류란? 이미 투자한 금액이 아깝다는 이유로 손실을 인정하지 못하는 심리 편향입니다. \n과거 비용은 회수할 수 없으므로 현재 시점에서 합리적 판단이 필요합니다',
        
        'FOMO': 'FOMO란? Fear Of Missing Out의 약자로, 급등 종목을 놓칠까봐 두려워 충분한 분석 없이 고점 매수하는 심리 편향입니다.\n 주의: 이미 상승한 종목은 조정 가능성이 높습니다'
    };
    
    const infoIcons = document.querySelectorAll('.info-icon');
    
    infoIcons.forEach(icon => {
        const biasType = icon.getAttribute('data-bias-type');
        const description = biasDescriptions[biasType] || '심리 편향에 대한 설명입니다.';
        icon.setAttribute('data-tooltip', description);
    });
}

/**
 * 심리경고 이벤트 리스너 설정
 */
function setupHistoryEventListeners() {
    const startDateInput = document.getElementById("alertStartDate");
    const endDateInput = document.getElementById("alertEndDate");
    
    if (startDateInput) {
        startDateInput.addEventListener("change", function() {
            console.log("심리경고 시작 날짜 변경:", this.value);
            loadHistoryData();
        });
    }
    
    if (endDateInput) {
        endDateInput.addEventListener("change", function() {
            console.log("심리경고 종료 날짜 변경:", this.value);
            loadHistoryData();
        });
    }
}

// ===========================
// 유틸리티 함수
// ===========================
function formatNumber(num) {
    return num.toLocaleString('ko-KR');
}

function formatCurrency(num) {
    return `${formatNumber(num)}원`;
}

function formatPercent(num) {
    const sign = num >= 0 ? '+' : '';
    return `${sign}${num}%`;
}
// ===== 알림에서 이동 시 스크롤 + 강조 =====

// URL 파라미터 읽기
function getUrlParams() {
    const params = new URLSearchParams(window.location.search);
    return {
        stock: params.get('stock'),
        bias: params.get('bias'),
        time: params.get('time')  // 시간 추가
    };
}

// 특정 경고로 스크롤 + 강조 (시간으로 정확히 매칭)
function scrollToAlert(stockCode, biasType, time) {
    console.log('[스크롤] 대상 찾기:', stockCode, biasType, time);
    
    // 모든 경고 카드 찾기
    const alerts = document.querySelectorAll('.mypage-warning-container');
    
    alerts.forEach(alert => {
        // data 속성에서 직접 가져오기
        const dataStockCode = alert.getAttribute('data-stock-code');
        const dataBiasType = alert.getAttribute('data-bias-type');
        const dataTime = alert.getAttribute('data-time');
        
        console.log('[스크롤] 검사 중:', dataStockCode, dataBiasType, dataTime);
        
        // 시간 비교 (둘 다 밀리초로 변환)
        const targetTime = new Date(time).getTime();
        const alertTime = parseInt(dataTime);  // 문자열 → 숫자 변환!
        
        console.log('[스크롤] 시간 비교:', targetTime, '===', alertTime, '?', targetTime === alertTime);
        
        // 정확히 일치하는 경고 찾기
        if (dataStockCode === stockCode && 
            dataBiasType === biasType && 
            targetTime === alertTime) {
            
            console.log('[스크롤] ✅ 정확한 대상 발견!', dataStockCode, dataBiasType, dataTime);
            
            // 스크롤
            setTimeout(() => {
                alert.scrollIntoView({
                    behavior: 'smooth',
                    block: 'center'
                });
                
                // 강조 효과
                alert.classList.add('highlight');
                
                // 3초 후 제거
                setTimeout(() => {
                    alert.classList.remove('highlight');
                }, 3000);
                
                // URL 파라미터 제거 (새로고침 시 정상 동작)
                window.history.replaceState({}, document.title, '/antmillion/mypage');
                console.log('[스크롤] URL 파라미터 제거 완료');
                
            }, 500);
        }
    });
}

// 페이지 로드 시 확인
window.addEventListener('load', function() {
    const params = getUrlParams();
    
    if (params.stock && params.bias && params.time) {
        console.log('[알림 이동] 파라미터 감지:', params);
        
        // 0.1초 대기 후 스크롤 (데이터 로딩 대기)
        setTimeout(() => {
            scrollToAlert(params.stock, params.bias, params.time);
        }, 100);
    }
});

// ===========================
// 페이지 떠날 때 구독 해제
// ===========================
window.addEventListener('beforeunload', function(e) {
    unsubscribeAllHoldingStocks();

    if (stompClient !== null && stompClient.connected) {
        stompClient.disconnect(function() {
            console.log('마이페이지 STOMP 연결 종료');
        });
    }
});

window.addEventListener('pagehide', function(e) {
    unsubscribeAllHoldingStocks();
});
