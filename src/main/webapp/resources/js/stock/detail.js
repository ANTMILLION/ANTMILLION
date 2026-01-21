// ===== 전역 변수 (community.js와 공유) =====
if (typeof contextPath === 'undefined') {
    var contextPath = '/antmillion';
    console.log('[contextPath 설정]', contextPath);
}

// ===========================
// API 호출 함수
// ===========================
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

        return await response.json();
    } catch (error) {
        console.error('[API 에러]', error);
        return null;
    }
}

// ===========================
// 매도 시 매매 편향 비동기 체크
// ===========================
async function checkSellBiasAsync(stockCode) {
    try {
        const biasData = await checkBiasAlert(stockCode);

        if (biasData && biasData.hasAlert) {
            console.log(
                '매매 편향 경고: ' +
                biasData.stockName + ' +' +
                biasData.profitRate + '% (' +
                biasData.holdingDays + '일 보유)'
            );

            // 경고 발생 트리거 (헤더 뱃지 + 알림 불)
            if (typeof onBiasWarningTriggered === 'function') {
                onBiasWarningTriggered();
            }
        } else {
            if (typeof hideBiasAlert === 'function') {
                hideBiasAlert();
            }
        }
    } catch (error) {
        console.error('[매매 편향 체크 실패]', error);
    }
}

// ===========================
// 차트 관련 로직
// ===========================
let stockChart = null;
let candleSeries = null;

function formatToTimestamp(dateStr, timeStr) {
    const year = parseInt(dateStr.substring(0, 4));
    const month = parseInt(dateStr.substring(4, 6)) - 1;
    const day = parseInt(dateStr.substring(6, 8));
    const hour = parseInt(timeStr.substring(0, 2));
    const minute = parseInt(timeStr.substring(2, 4));
    const second = parseInt(timeStr.substring(4, 6)) || 0;

    const date = new Date(year, month, day, hour, minute, second);
    return Math.floor(date.getTime() / 1000);
}

function drawDetailChart(stockCode) {
    const chartContainer = document.getElementById('detail-stockChart');
    if (!chartContainer) return;

    chartContainer.innerHTML = '';

    stockChart = LightweightCharts.createChart(chartContainer, {
        width: chartContainer.clientWidth,
        height: chartContainer.clientHeight,
        timeScale: { timeVisible: true },
        layout: {
            background: { type: 'solid', color: 'white' },
            textColor: 'black'
        }
    });

    candleSeries = stockChart.addCandlestickSeries({
        upColor: '#e74c3c',
        downColor: '#3498db',
        wickUpColor: '#e74c3c',
        wickDownColor: '#3498db'
    });

    fetch(contextPath + '/api/kis/stream/' + stockCode)
        .then(res => res.json())
        .then(data => {
            const chartData = data.map(row => ({
                time: formatToTimestamp(row.stck_bsop_date, row.stck_cntg_hour),
                open: +row.stck_oprc,
                high: +row.stck_hgpr,
                low: +row.stck_lwpr,
                close: +row.stck_prpr
            }));
            candleSeries.setData(chartData);
        });
}

// ===========================
// DOM 로드 후 실행
// ===========================
document.addEventListener('DOMContentLoaded', function () {
    console.log('=== 종목 상세 페이지 로드 완료 ===');

    // 차트
    const stockCode = new URLSearchParams(location.search).get('code');
    if (stockCode) {
        drawDetailChart(stockCode);
    }

    // 관심종목 상태
    checkFavoriteStatus();

    // 탭 이벤트
    const tabs = document.querySelectorAll('.detail-tab, .detail-tab-active');
    tabs.forEach(tab => {
        tab.addEventListener('click', function () {
            const type = this.dataset.type;

            if (type === 'sell' && stockCode) {
                checkSellBiasAsync(stockCode);
            }
        });
    });
});

// ===========================
// 관심종목 로직
// ===========================
function checkFavoriteStatus() {
    const btn = document.querySelector('.detail-favorite-btn');
    if (!btn) return;

    const stockCode = btn.dataset.code;

    fetch(contextPath + '/api/interest/list')
        .then(res => res.json())
        .then(list => {
            const isFavorite = list.includes(stockCode);
            btn.textContent = isFavorite ? '♥' : '♡';
            btn.classList.toggle('active', isFavorite);
        });
}

document.querySelector('.detail-favorite-btn')?.addEventListener('click', function () {
    const stockCode = this.dataset.code;

    fetch(contextPath + '/api/interest/toggle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ stockCode })
    })
        .then(res => res.json())
        .then(data => {
            this.classList.toggle('active', data.isInterest);
            this.textContent = data.isInterest ? '♥' : '♡';
        });
});
