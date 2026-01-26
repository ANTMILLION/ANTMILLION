/* ===========================
   종목 리스트 페이지 JavaScript
   =========================== */

let currentTab = 'all'; // 'all' 또는 'favorite'
let currentPage = 1;
const itemsPerPage = 10;
let totalPages = 1;

// STOMP 관련 변수
let stompClient = null;
let subscribedTopics = {}; // 구독한 토픽 저장 {stockCode: subscription}
let currentStockCodes = []; // 현재 화면에 표시된 종목 코드들

// 임시 account_id (로그인 기능 완성 전까지)
const TEMP_ACCOUNT_ID = 3;

// ========== STOMP 연결 ==========
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

// ========== 현재 화면 종목들 구독 ==========
function subscribeCurrentStocks() {
    // 기존 구독 모두 해제
    unsubscribeAllStocks();

    if (currentStockCodes.length === 0) return;

    // ✅ Mock 모드일 때는 한투 백엔드 구독 건너뛰고 STOMP만 구독
    if (isMockMode) {
        console.log('Mock 모드 - STOMP 구독만 진행');
        currentStockCodes.forEach(stockCode => {
            subscribeStockTopic(stockCode);
        });
        return;
    }

    // 백엔드에 구독 요청
    fetch(contextPath + '/api/kis/websocket/subscribe-multiple?trId=H0STCNT0', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(currentStockCodes)
    })
        .then(res => res.json())
        .then(response => {
            console.log('백엔드 구독 완료:', response);

            // 프론트엔드 STOMP 토픽 구독
            currentStockCodes.forEach(stockCode => {
                subscribeStockTopic(stockCode);
            });
        })
        .catch(error => {
            console.error('백엔드 구독 실패:', error);
        });
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

// ========== 실시간 가격 화면 업데이트 ==========
function updateStockRealtimePrice(stockCode, tradeData) {
    const stockItem = document.querySelector(`.stocklist-item[data-code="${stockCode}"]`);
    if (!stockItem) return;

    // 현재가 업데이트
    const priceElement = stockItem.querySelector('.stocklist-price');
    if (priceElement) {
        const price = Number(tradeData.stckPrpr).toLocaleString('ko-KR') + '원';
        priceElement.textContent = price;
    }

    // 등락률 업데이트
    const changeElement = stockItem.querySelector('.stocklist-change');
    if (changeElement) {
        changeElement.textContent = tradeData.prdySign + tradeData.prdyCtrt + '%';

        // 색상 변경
        changeElement.classList.remove('positive', 'negative');

        if (tradeData.prdySign === '+') {
            changeElement.classList.add('positive');
        } else if (tradeData.prdySign === '-') {
            changeElement.classList.add('negative');
        }
    }

    // 거래 비율 업데이트
    if (tradeData.shnuRate) {
        const buyRate = parseFloat(tradeData.shnuRate);
        const sellRate = 100 - buyRate;

        const buyBar = stockItem.querySelector('.stocklist-sentiment-buy');
        const sellBar = stockItem.querySelector('.stocklist-sentiment-sell');
        const buyLabel = stockItem.querySelector('.stocklist-sentiment-buy-label');
        const sellLabel = stockItem.querySelector('.stocklist-sentiment-sell-label');

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
        fetch(contextPath + '/api/kis/websocket/unsubscribe-all?trId=H0STCNT0', {
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

// ========== 종목 아이템 HTML 생성 함수 ==========
function createStockItemHTML(stock, index, isFavorite) {
    const favoriteIcon = isFavorite ? '♥' : '♡';
    const favoriteClass = isFavorite ? 'active' : '';

    // 현재가 포맷팅
    const currentPrice = Number(stock.stck_prpr || 0).toLocaleString('ko-KR') + '원';

    // 등락률 계산
    const priceChange = Number(stock.prdy_vrss || 0);
    const changeRate = Number(stock.prdy_ctrt || 0);
    const changeSign = priceChange > 0 ? '+' : (priceChange < 0 ? '-' : '');
    const changeClass = priceChange > 0 ? 'positive' : (priceChange < 0 ? 'negative' : '');

    return `
        <div class="stocklist-item" data-code="${stock.mksc_shrn_iscd}">
            <div class="stocklist-favorite">
                <span class="stocklist-rank">${index + 1}</span>
                <button class="stocklist-favorite-btn ${favoriteClass}" 
                        data-code="${stock.mksc_shrn_iscd}">${favoriteIcon}</button>
            </div>
            <div class="stocklist-info">
                <div class="stocklist-logo">
                    <img src="" alt="${stock.hts_kor_isnm}">
                </div>
                <span class="stocklist-name">${stock.hts_kor_isnm}</span>
            </div>
            <div class="stocklist-price">${currentPrice}</div>
            <div class="stocklist-change ${changeClass}">${changeSign}${Math.abs(changeRate).toFixed(2)}%</div>
            <div class="stocklist-sentiment">
                <div class="stocklist-sentiment-bar">
                    <div class="stocklist-sentiment-buy" style="width: 50%;"></div>
                    <div class="stocklist-sentiment-sell" style="width: 50%;"></div>
                </div>
                <div class="stocklist-sentiment-labels">
                    <span class="stocklist-sentiment-buy-label">50</span>
                    <span class="stocklist-sentiment-sell-label">50</span>
                </div>
            </div>
        </div>
    `;
}

// ========== 전체 종목 렌더링 (페이징) ==========
function renderAllStocks() {
    const container = document.getElementById('stocklist-Container');

    fetch(`${contextPath}/api/kis/volumeRank/paged?page=${currentPage}&size=${itemsPerPage}`)
        .then(res => res.json())
        .then(responseData => {
            const stocks = responseData.data;
            totalPages = responseData.totalPages;

            // 현재 화면의 종목 코드들 저장
            currentStockCodes = stocks.map(stock => stock.mksc_shrn_iscd);

            // 관심종목 목록 가져오기
            return fetch(`${contextPath}/api/interest/list`)
                .then(res => res.json())
                .then(interestCodes => {
                    const startIndex = (currentPage - 1) * itemsPerPage;
                    const html = stocks.map((stock, index) => {
                        const isFavorite = interestCodes.includes(stock.mksc_shrn_iscd);
                        return createStockItemHTML(stock, startIndex + index, isFavorite);
                    }).join('');

                    container.innerHTML = html;

                    // 페이지네이션 렌더링
                    renderPagination();

                    // 이벤트 리스너 재등록
                    attachFavoriteListeners();
                    attachStockItemListeners();

                    // STOMP 연결 확인 후 구독
                    if (stompClient && stompClient.connected) {
                        subscribeCurrentStocks();
                    }
                });
        })
        .catch(error => {
            console.error('종목 데이터 로드 실패:', error);
            container.innerHTML = '<p>종목 데이터를 불러오는데 실패했습니다.</p>';
        });
}

// ========== 관심종목 렌더링 ==========
function renderFavoriteStocks() {
    const container = document.getElementById('stocklist-Container');

    // 관심종목 코드 목록 가져오기
    fetch(`${contextPath}/api/interest/list`)
        .then(res => res.json())
        .then(interestCodes => {
            if (interestCodes.length === 0) {
                container.innerHTML = '<p style="text-align: center; padding: 40px; color: #9CA3AF;">관심종목이 없습니다.</p>';
                document.getElementById('pagination-container')?.remove();
                currentStockCodes = [];
                unsubscribeAllStocks();
                return;
            }

            // 현재 화면의 종목 코드들 저장
            currentStockCodes = interestCodes;

            // 관심종목 코드로 종목 정보 가져오기
            return fetch(`${contextPath}/api/kis/stocksByCode`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ stockCodes: interestCodes })
            })
                .then(res => res.json())
                .then(stocks => {
                    const html = stocks.map((stock, index) => {
                        return createStockItemHTML(stock, index, true);
                    }).join('');

                    container.innerHTML = html;

                    // 관심종목 탭에서는 페이지네이션 숨기기
                    document.getElementById('pagination-container')?.remove();

                    // 이벤트 리스너 재등록
                    attachFavoriteListeners();
                    attachStockItemListeners();

                    // STOMP 연결 확인 후 구독
                    if (stompClient && stompClient.connected) {
                        subscribeCurrentStocks();
                    }
                });
        })
        .catch(error => {
            console.error('관심종목 데이터 로드 실패:', error);
            container.innerHTML = '<p>관심종목 데이터를 불러오는데 실패했습니다.</p>';
        });
}

// ========== 종목 리스트 렌더링 (탭에 따라 분기) ==========
function renderStocks() {
    if (currentTab === 'all') {
        renderAllStocks();
    } else {
        renderFavoriteStocks();
    }
}

// ========== 페이지네이션 렌더링 ==========
function renderPagination() {
    // 기존 페이지네이션 제거
    const existingPagination = document.getElementById('pagination-container');
    if (existingPagination) {
        existingPagination.remove();
    }

    // 전체 종목 탭이 아니면 페이지네이션 표시 안함
    if (currentTab !== 'all') {
        return;
    }

    const stocklistCard = document.querySelector('.stocklist-card');
    const paginationContainer = document.createElement('div');
    paginationContainer.id = 'pagination-container';
    paginationContainer.className = 'pagination-container';

    let paginationHTML = '';

    // 이전 버튼
    if (currentPage > 1) {
        paginationHTML += `<button class="pagination-btn" data-page="${currentPage - 1}">이전</button>`;
    }

    // 페이지 번호
    for (let i = 1; i <= totalPages; i++) {
        const activeClass = i === currentPage ? 'active' : '';
        paginationHTML += `<button class="pagination-btn ${activeClass}" data-page="${i}">${i}</button>`;
    }

    // 다음 버튼
    if (currentPage < totalPages) {
        paginationHTML += `<button class="pagination-btn" data-page="${currentPage + 1}">다음</button>`;
    }

    paginationContainer.innerHTML = paginationHTML;
    stocklistCard.appendChild(paginationContainer);

    // 페이지네이션 버튼 이벤트 리스너
    document.querySelectorAll('.pagination-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            currentPage = parseInt(this.getAttribute('data-page'));
            renderStocks();
        });
    });
}

// ========== 즐겨찾기 버튼 이벤트 리스너 등록 ==========
function attachFavoriteListeners() {
    document.querySelectorAll('.stocklist-favorite-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const stockCode = this.getAttribute('data-code');

            // 서버에 관심종목 토글 요청
            fetch(`${contextPath}/api/interest/toggle`, {
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

                        // 관심종목 탭이면 목록 새로고침
                        if (currentTab === 'favorite') {
                            renderStocks();
                        }
                    }
                })
                .catch(error => {
                    console.error('관심종목 토글 실패:', error);
                });
        });
    });
}

// ========== 종목 아이템 클릭 이벤트 리스너 등록 ==========
function attachStockItemListeners() {
    document.querySelectorAll('.stocklist-item').forEach(item => {
        item.addEventListener('click', function(e) {
            // 즐겨찾기 버튼 클릭은 제외
            if (e.target.closest('.stocklist-favorite-btn')) {
                return;
            }

            const code = this.dataset.code;
            window.location.href = contextPath + '/stock/detail?code=' + code;
        });
    });
}

// ========== 탭 전환 이벤트 ==========
document.querySelectorAll('.stocklist-tab-btn').forEach((btn, index) => {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.stocklist-tab-btn').forEach(b => b.classList.remove('active'));
        this.classList.add('active');

        // 첫 번째 버튼: 전체종목, 두 번째 버튼: 관심종목
        currentTab = index === 0 ? 'all' : 'favorite';
        currentPage = 1; // 탭 변경 시 페이지 초기화
        renderStocks();
    });
});

// ========== 페이지 떠날 때 전체 구독 해제 ==========
window.addEventListener('beforeunload', function(e) {
    unsubscribeAllStocks();

    if (stompClient !== null && stompClient.connected) {
        stompClient.disconnect(function() {
            console.log('STOMP 연결 종료');
        });
    }
});

window.addEventListener('pagehide', function(e) {
    unsubscribeAllStocks();
});

// ========== 초기화 ==========
document.addEventListener('DOMContentLoaded', function() {
    // STOMP 연결
    connectStomp();

    // 초기 렌더링
    renderStocks();
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
