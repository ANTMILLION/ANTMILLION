// ===== Mock 데이터 (테스트용) =====
const mockStockData = {
    stockCode: '005930',
    stockName: '삼성전자',
    avgPrice: 70000,      // 평균 매수가
    currentPrice: 72100,  // 현재가
    quantity: 10,
    purchaseDate: '2025-01-17',
    holdingDays: 2
};

// 수익률 계산
function calculateProfitRate() {
    const profit = mockStockData.currentPrice - mockStockData.avgPrice;
    const profitRate = (profit / mockStockData.avgPrice) * 100;
    return profitRate.toFixed(2);
}

// 안전선호 체크 (수익률 3% 이상)
function checkSafeHavenBias() {
    const profitRate = parseFloat(calculateProfitRate());
    return profitRate >= 3;
}

// ===== 탭 전환 기능 =====
document.querySelectorAll('.detail-tab, .detail-tab-active').forEach(tab => {
    tab.addEventListener('click', function() {
        const type = this.dataset.type;
        if (type === 'pending') { 
            alert('대기 주문 기능은 준비 중입니다.'); 
            return; 
        }
        
        // 모든 탭에서 active 클래스 제거
        document.querySelectorAll('.detail-tab, .detail-tab-active').forEach(t => {
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
               
        } else if (type === 'sell') {
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
            
            // ⭐ 매도 탭 클릭 시 안전선호 체크
            if (checkSafeHavenBias()) {
                const profitRate = calculateProfitRate();
                console.log(`안전선호 경고: 현재 +${profitRate}% 수익 중`);
                
                // 헤더에 경고 표시
                if (typeof showBiasAlert === 'function') {
                    showBiasAlert();
                }
            }
        }
    });
});

// ===== 디버그용 콘솔 출력 =====
console.log('=== 매매 편향 체크 시스템 로드 완료 ===');
console.log('Mock 데이터:', mockStockData);
console.log('현재 수익률:', calculateProfitRate() + '%');
console.log('안전선호 편향:', checkSafeHavenBias() ? '감지됨 (3% 이상)' : '정상');