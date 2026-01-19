// 탭 전환 기능
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
        }
    });
});
