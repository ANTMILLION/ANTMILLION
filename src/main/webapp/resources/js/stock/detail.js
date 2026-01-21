// ===== 전역 변수 (community.js와 공유) =====
// const 대신 var 사용으로 전역 스코프에 등록
if (typeof contextPath === 'undefined') {
    var contextPath = '/antmillion';
    console.log('[contextPath 설정]', contextPath);
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

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const stockCode = urlParams.get('code'); // ?code=005930 에서 005930 추출

    if (stockCode) {
        drawDetailChart(stockCode);
    } else {
        console.error("URL에 종목 코드가 없습니다.");
    }
});

// 반응형 대응
window.addEventListener('resize', () => {
    if (stockChart) {
        const container = document.querySelector('.detail-chart-area');
        stockChart.resize(container.clientWidth, container.clientHeight);
    }
});

// ===== DOM 로드 후 실행 =====
document.addEventListener('DOMContentLoaded', function() {
    checkFavoriteStatus();
    console.log('=== 매매 편향 체크 시스템 로드 완료 (API 연동) ===');
    
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
                
                // 매수 탭에서는 경고를 유지 (숨기지 않음)
                console.log('[경고] 매수 탭 - 경고 유지');
                   
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
                
                // 매도 탭 클릭 시 API로 위험회피  체크
                const urlParams = new URLSearchParams(window.location.search);
                const stockCode = urlParams.get('code');
                console.log('[종목 코드]', stockCode);
                
                if (stockCode) {
                    const biasData = await checkBiasAlert(stockCode);
                    
                    if (biasData && biasData.hasAlert) {
                        console.log('위험회피  경고: ' + biasData.stockName + ' +' + biasData.profitRate + '% 수익 중 (' + biasData.holdingDays + '일 보유)');
                        
                        // 헤더에 경고 표시
                        if (typeof showBiasAlert === 'function') {
                            showBiasAlert();
                        } else {
                            console.error('showBiasAlert 함수 없음!');
                        }
                    } else {
                        if (biasData) {
                            console.log('편향 없음 - 수익률: ' + biasData.profitRate + '%, 보유일수: ' + biasData.holdingDays + '일');
                        }
                        
                        // 경고 조건 미달 시에만 숨김
                        if (typeof hideBiasAlert === 'function') {
                            hideBiasAlert();
                        }
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


