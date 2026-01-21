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
                
                // 매도 탭 클릭 시 API로 안전선호 체크
                const urlParams = new URLSearchParams(window.location.search);
                const stockCode = urlParams.get('code');
                console.log('[종목 코드]', stockCode);
                
                if (stockCode) {
                    const biasData = await checkBiasAlert(stockCode);
                    
                    if (biasData && biasData.hasAlert) {
                        console.log('안전선호 경고: ' + biasData.stockName + ' +' + biasData.profitRate + '% 수익 중 (' + biasData.holdingDays + '일 보유)');
                        
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


