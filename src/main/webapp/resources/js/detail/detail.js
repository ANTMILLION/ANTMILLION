// ===== 전역 변수 (community.js와 공유) =====
if (typeof contextPath === 'undefined') {
    var contextPath = window.location.pathname.split('/')[1] || 'antmillion';
    console.log('[contextPath 설정]', contextPath);
}

// ===========================
// API 호출 함수
// ===========================
async function checkBiasAlert(stockCode) {
    console.log('[API 호출] stockCode:', stockCode);

    try {
        const url = '/' + contextPath + '/api/bias-alert/check?stockCode=' + stockCode;
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

// ===========================
// 매도 시 위험회피 비동기 체크 
// ===========================
async function checkSellBiasAsync(stockCode) {
    try {
        const biasData = await checkBiasAlert(stockCode);

        if (biasData && biasData.hasAlert) {
            console.log(
                '위험회피 경고: ' +
                biasData.stockName + ' +' +
                biasData.profitRate + '% 수익 중 (' +
                biasData.holdingDays + '일 보유)'
            );

            // 경고 발생 트리거 (뱃지 + 알림 빨간 불)
            if (typeof onBiasWarningTriggered === 'function') {
                onBiasWarningTriggered();
            } else {
                console.error('onBiasWarningTriggered 함수 없음!');
            }

        } else {
            if (biasData) {
                console.log(
                    '편향 없음 - 수익률: ' +
                    biasData.profitRate + '%, 보유일수: ' +
                    biasData.holdingDays + '일'
                );
            }

            // 경고 조건 미달 시에만 숨김
            if (typeof hideBiasAlert === 'function') {
                hideBiasAlert();
            }
        }
    } catch (error) {
        console.error('[위험회피 체크 실패]', error);
    }
}

// ===========================
// DOM 로드 후 실행
// ===========================
document.addEventListener('DOMContentLoaded', function () {
    console.log('=== 매매 편향 체크 시스템 로드 완료 (최종본) ===');

    const tabs = document.querySelectorAll('.detail-tab, .detail-tab-active');
    console.log('[탭 개수]', tabs.length);

    if (tabs.length === 0) {
        console.error('탭을 찾을 수 없습니다!');
        return;
    }

    // ===========================
    // 탭 전환 기능
    // ===========================
    tabs.forEach(function (tab) {
        tab.addEventListener('click', function () {
            const type = this.dataset.type;
            console.log('[탭 클릭]', type);

            if (type === 'pending') {
                alert('대기 주문 기능은 준비 중입니다.');
                return;
            }

            // 탭 UI 처리
            document.querySelectorAll('.detail-tab, .detail-tab-active').forEach(function (t) {
                t.classList.remove('detail-tab-active');
                t.classList.add('detail-tab');
            });

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

            // ===========================
            // 매수 탭
            // ===========================
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

                console.log('[경고] 매수 탭 - 경고 유지');
            }

            // ===========================
            // 매도 탭
            // ===========================
            else if (type === 'sell') {
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

                const urlParams = new URLSearchParams(window.location.search);
                const stockCode = urlParams.get('code');
                console.log('[종목 코드]', stockCode);

                if (stockCode) {
                    checkSellBiasAsync(stockCode);
                } else {
                    console.warn('URL에 종목 코드(code) 없음');
                }
            }
        });
    });
});
