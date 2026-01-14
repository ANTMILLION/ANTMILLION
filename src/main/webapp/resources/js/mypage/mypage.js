// ===========================
// 마이페이지 JavaScript (mypage.js)
// ===========================

// 샘플 데이터
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
                time: '12:20:11'
            },
            {
                stock: '삼성전자',
                type: 'buy',
                orderQty: 10,
                executedQty: 10,
                unexecutedQty: 0,
                unexecutedAmount: 0,
                time: '11:20:11'
            },
            {
                stock: 'SK하이닉스',
                type: 'buy',
                orderQty: 1,
                executedQty: 0,
                unexecutedQty: 1,
                unexecutedAmount: 742000,
                time: '10:20:11'
            }
        ]
    },
    
    // 매매내역 데이터
    tradingHistory: [
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
    ],
    
    // 심리경고 알림 데이터
    alerts: [
        {
            date: '2026.01.02',
            stock: '신한지주',
            type: 'buy',
            shares: 10,
            price: 700000,
            changeRate: -10
        }
    ]
};

// ===========================
// DOM 요소
// ===========================
let stockList, realizedList, executedTable, tradingList, alertList;
let tabButtons, subTabButtons, filterButtons;

// ===========================
// 초기화
// ===========================
document.addEventListener('DOMContentLoaded', function() {
    initializeElements();
    setupEventListeners();
    renderInitialData();
});

function initializeElements() {
    // DOM 요소 가져오기
    stockList = document.getElementById('stockList');
    realizedList = document.getElementById('realizedList');
    executedTable = document.getElementById('executedTable');
    tradingList = document.getElementById('tradingList');
    alertList = document.getElementById('alertList');
    
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
    renderStockHoldings();
    renderRealizedProfits();
    renderExecutedOrders('all');
    renderTradingHistory('all');
    renderAlerts();
}

// ===========================
// 주식잔고 렌더링
// ===========================
function renderStockHoldings() {
    if (!stockList) return;
    
    stockList.innerHTML = sampleData.stockHoldings.map(stock => {
        const profitClass = stock.profit > 0 ? 'positive' : stock.profit <  0 ? 'negative' : 'neutral';
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
            <tr>
                <td>
                    <div class="mypage-stock-name-cell">${order.stock}</div>
                    <span class="${typeClass}">${typeText}</span>
                </td>
                <td>${order.orderQty}</td>
                <td>${order.executedQty}</td>
                <td>${order.unexecutedQty}</td>
                <td>${order.unexecutedAmount === 0 ? '0' : order.unexecutedAmount.toLocaleString()}</td>
                <td>${order.time}</td>
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

// ===========================
// 심리경고 알림 렌더링
// ===========================
function renderAlerts() {
    if (!alertList) return;
    
    alertList.innerHTML = sampleData.alerts.map(alert => {
        const typeText = alert.type === 'buy' ? '매수' : '매도';
        const changeClass = alert.changeRate >= 0 ? 'positive' : 'negative';
        const changeSign = alert.changeRate >= 0 ? '+' : '';
        
        return `
            <div class="mypage-alert-item">
                <div class="mypage-alert-item-date">${alert.date}</div>
                <div class="mypage-alert-item-stock">${alert.stock}</div>
                <div class="mypage-alert-item-detail">${typeText} ${alert.shares}주</div>
                <div class="mypage-alert-item-price ${changeClass}">
                    ${alert.price.toLocaleString()}원 (${changeSign}${alert.changeRate}%)
                </div>
            </div>
        `;
    }).join('');
}

// ===========================
// 알림 필터 변경
// ===========================
const alertFilter = document.getElementById('alertFilter');
if (alertFilter) {
    alertFilter.addEventListener('change', function() {
        const filterValue = this.value;
        // 여기에 필터링 로직 추가
        console.log('Alert filter changed to:', filterValue);
    });
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
