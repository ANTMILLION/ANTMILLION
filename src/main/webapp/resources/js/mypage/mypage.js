// ===========================
// BiasType 매핑
// ===========================
const biasTypeMap = {
    RISK_AVERSION: { title: "위험회피 주의", icon: "⚠️" },
    LOSS_AVERSION: { title: "손실회피 주의", icon: "⚠️" },
    SUNK_COST: { title: "매몰비용오류 경고", icon: "⚠️" },
    FOMO: { title: "FOMO 주의", icon: "⚠️" }
};

// ===========================
// 샘플 데이터
// ===========================
const sampleData = {
    // 주식잔고 데이터
    stockHoldings: [
        {
            name: '엔트밀리언',
            shares: 10,
            buyPrice: 100000,
            currentPrice: 5000,
            profit: -50000,
            profitRate: -50.00,
            avgBuyPrice: 10000,
            totalValue: 50000
        },
        {
            name: '삼성전자',
            shares: 10,
            buyPrice: 1017000,
            currentPrice: 1293000,
            profit: 276000,
            profitRate: 27.14,
            avgBuyPrice: 77000,
            totalValue: 129300
        },
        {
            name: '삼성전자',
            shares: 10,
            buyPrice: 1017000,
            currentPrice: 1293000,
            profit: 276000,
            profitRate: 27.14,
            avgBuyPrice: 77000,
            totalValue: 129300
        },
        {
            name: '신한지주',
            shares: 20,
            buyPrice: 1540000,
            currentPrice: 1540000,
            profit: 0,
            profitRate: 0,
            avgBuyPrice: 77000,
            totalValue: 77000
        }
    ],
    
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
    
    initializeElements();
    setupEventListeners();
    renderInitialData();
    
    // ✅ 심리경고 전용 초기화
    initializeHistoryDates();
    loadHistoryData();
    setupHistoryEventListeners();
});

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
// 이벤트 리스너 설정
// ===========================
function setupEventListeners() {
    // 메인 탭 전환
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            switchTab(this.dataset.tab);
        });
    });
    
    // 서브 탭 전환 (체결내역)
    subTabButtons.forEach(button => {
        button.addEventListener('click', function() {
            switchSubTab(this.dataset.subtab);
        });
    });
    
    // 필터 버튼 (매매내역)
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            filterTradingHistory(this.dataset.filter);
        });
    });
    
    // 기간 선택 초기화
    setupDateFilters();
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
    loadStockHoldings(); // ✅ 이 함수가 서버에서 데이터를 가져온 후 renderStockHoldings를 실행함
    renderRealizedProfits();
    renderExecutedOrders('all');
    renderTradingHistory('all');
}

// ✅ DB에서 주식 잔고 데이터를 가져오는 함수 (새로 추가)
function loadStockHoldings() {
    fetch(contextPath+'/mypage/api/stock-holdings') // 앞서 만든 컨트롤러 URL
        .then(res => res.json())
        .then(data => {
            renderStockHoldings(data); // 데이터를 받아서 렌더링 함수에 전달
        })
        .catch(err => {
            console.error("주식 잔고 로드 실패:", err);
            if (stockList) stockList.innerHTML = "<p class='no-data'>데이터 로드 실패</p>";
        });
}

// ===========================
// 주식잔고 렌더링 (수정)
// ===========================
function renderStockHoldings(holdings) { // ✅ 매개변수 추가
    if (!stockList) return;
    
    // ✅ 데이터가 없을 때 처리 추가
    if (!holdings || holdings.length === 0) {
        stockList.innerHTML = "<p class='no-data'>보유 중인 주식이 없습니다.</p>";
        return;
    }
    
    // ✅ sampleData.stockHoldings 대신 holdings 사용
    stockList.innerHTML = holdings.map(stock => {
        const profitClass = stock.profit > 0 ? 'positive' : stock.profit < 0 ? 'negative' : 'neutral';
        const profitSign = stock.profit > 0 ? '+' : '';
        
        return `
            <div class="mypage-stock-card">
                <div class="mypage-stock-header">
                    <div>
                        <div class="mypage-stock-name">${stock.name}</div>
                        <div class="mypage-stock-shares">현금 ${stock.shares}주</div>
                    </div>
                    <div class="mypage-stock-profit ${profitClass}">
                        ${profitSign}${stock.profit.toLocaleString()}원(${profitSign}${stock.profitRate}%)
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
                        <span class="mypage-detail-value">${stock.totalValue.toLocaleString()}원</span>
                    </div>
                    <div class="mypage-stock-detail-item">
                        <span class="mypage-detail-label">현재가</span>
                        <span class="mypage-detail-value">${stock.currentPrice.toLocaleString()}원</span>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// ===========================
// 실현손익 렌더링
// ===========================
function renderRealizedProfits() {
    if (!realizedList) return;
    
    realizedList.innerHTML = sampleData.realizedProfits.map(period => {
        const isProfitable = period.amount >= 0;
        const profitClass = isProfitable ? 'positive' : 'negative';
        const profitSign = isProfitable ? '+' : '';
        
        return `
            <div class="mypage-realized-item">
                <div class="mypage-realized-header">
                    <span class="mypage-realized-date">${period.date}</span>
                    <span class="mypage-realized-amount ${profitClass}">
                        ${profitSign}${period.amount.toLocaleString()}원
                    </span>
                </div>
                <div class="mypage-realized-detail">
                    ${period.items.map(item => `
                        <div>
                            <div class="mypage-realized-stock">${item.stock}</div>
                            <div class="mypage-realized-info">
                                ${item.date} <span class="${item.amount >= 0 ? 'positive' : 'negative'}">${item.amount >= 0 ? '+' : ''}${item.amount.toLocaleString()}원</span>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }).join('');
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

// ===========================
// 매매내역 렌더링
// ===========================
function renderTradingHistory(filter) {
    if (!tradingList) return;
    
    let history = sampleData.tradingHistory;
    
    // 필터 적용
    if (filter === 'buy') {
        history = history.filter(item => item.type === 'buy');
    } else if (filter === 'sell') {
        history = history.filter(item => item.type === 'sell');
    }
    
    tradingList.innerHTML = history.map(item => {
        const typeClass = item.type === 'buy' ? 'mypage-buy-badge' : 'mypage-sell-badge';
        const typeText = item.type === 'buy' ? '매수' : '매도';
        
        return `
            <div class="mypage-trading-item">
                <div class="mypage-trading-date">${item.date}</div>
                <div class="mypage-trading-stock-info">
                    <div>
                        <span class="mypage-trading-stock-name">${item.stock}</span>
                        <span class="${typeClass}">${typeText} ${item.shares}주</span>
                    </div>
                    <div class="mypage-trading-amount">${item.amount.toLocaleString()}원</div>
                </div>
                <div class="mypage-trading-detail">${item.detail}</div>
            </div>
        `;
    }).join('');
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

                //  수정된 HTML 구조
                const html = `
                    <div class="mypage-warning-container">
                        <div class="mypage-warning-header">
                            <span class="warning-icon">⚠️</span>
                            <span class="warning-title">${biasInfo.title}</span>
                            <span class="info-icon">ⓘ</span>
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
        })
        .catch(error => {
            console.error("심리경고 데이터 로드 실패:", error);
            alertList.innerHTML = "<p class='no-data'>심리경고 데이터를 불러오지 못했습니다.</p>";
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