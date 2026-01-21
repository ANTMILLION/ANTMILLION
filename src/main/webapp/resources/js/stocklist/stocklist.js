/* ===========================
   종목 리스트 페이지 JavaScript
   =========================== */

let currentTab = 'all'; // 'all' 또는 'favorite'
let currentPage = 1;
const itemsPerPage = 10;
let totalPages = 1;

// 임시 account_id (로그인 기능 완성 전까지)
const TEMP_ACCOUNT_ID = 3;

// 종목 아이템 HTML 생성 함수
function createStockItemHTML(stock, index, isFavorite) {
    const favoriteIcon = isFavorite ? '♥' : '♡';
    const favoriteClass = isFavorite ? 'active' : '';

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
            <div class="stocklist-price">${stock.stck_prpr}</div>
            <div class="stocklist-change">${stock.acml_vol}</div>
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

// 전체 종목 렌더링 (페이징)
function renderAllStocks() {
    const container = document.getElementById('stocklist-Container');

    fetch(`${contextPath}/api/kis/volumeRank/paged?page=${currentPage}&size=${itemsPerPage}`)
        .then(res => res.json())
        .then(responseData => {
            const stocks = responseData.data;
            totalPages = responseData.totalPages;

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
                });
        })
        .catch(error => {
            console.error('종목 데이터 로드 실패:', error);
            container.innerHTML = '<p>종목 데이터를 불러오는데 실패했습니다.</p>';
        });
}

// 관심종목 렌더링
function renderFavoriteStocks() {
    const container = document.getElementById('stocklist-Container');

    // 관심종목 코드 목록 가져오기
    fetch(`${contextPath}/api/interest/list`)
        .then(res => res.json())
        .then(interestCodes => {
            if (interestCodes.length === 0) {
                container.innerHTML = '<p style="text-align: center; padding: 40px; color: #9CA3AF;">관심종목이 없습니다.</p>';
                document.getElementById('pagination-container')?.remove();
                return;
            }

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
                });
        })
        .catch(error => {
            console.error('관심종목 데이터 로드 실패:', error);
            container.innerHTML = '<p>관심종목 데이터를 불러오는데 실패했습니다.</p>';
        });
}

// 종목 리스트 렌더링 (탭에 따라 분기)
function renderStocks() {
    if (currentTab === 'all') {
        renderAllStocks();
    } else {
        renderFavoriteStocks();
    }
}

// 페이지네이션 렌더링
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

// 즐겨찾기 버튼 이벤트 리스너 등록
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

// 종목 아이템 클릭 이벤트 리스너 등록
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

// 탭 전환 이벤트
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

// 초기 렌더링
document.addEventListener('DOMContentLoaded', function() {
    renderStocks();
});
