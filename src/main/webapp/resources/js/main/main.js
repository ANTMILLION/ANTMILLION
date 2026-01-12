/* ===========================
   메인 페이지 JavaScript (main.js)
   =========================== */

// 차트 인스턴스 저장
let ethereumChart = null;
let bitcoinChart = null;

// DOM이 로드되면 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeMainPage();
});

function initializeMainPage() {
    // 차트 초기화
    initializeCharts();
    
    // 랭크 이미지 업로드 기능
    initializeRankImageUpload();
    
    // 미션 버튼 이벤트
    initializeMissionButton();
    
    // 주식 테이블 인터랙션
    initializeStockTable();
    
    // 실시간 데이터 업데이트 (선택사항)
    // startRealtimeUpdates();
}

// 차트 초기화 (Chart.js 사용 가정)
function initializeCharts() {
    // Chart.js가 로드되어 있는지 확인
    if (typeof Chart === 'undefined') {
        // Chart.js가 없으면 간단한 SVG 차트로 대체
        drawSimpleChart('ethereumChart', generateChartData(30, 20000, 25000, true));
        drawSimpleChart('bitcoinChart', generateChartData(30, 20000, 25000, false));
        return;
    }
    
    // Ethereum 차트
    const ethCanvas = document.getElementById('ethereumChart');
    if (ethCanvas) {
        ethereumChart = createCryptoChart(ethCanvas, {
            color: '#8B9FFF',
            gradient1: 'rgba(139, 159, 255, 0.3)',
            gradient2: 'rgba(139, 159, 255, 0)',
            data: generateChartData(30, 20000, 25000, true)
        });
    }
    
    // Bitcoin 차트
    const btcCanvas = document.getElementById('bitcoinChart');
    if (btcCanvas) {
        bitcoinChart = createCryptoChart(btcCanvas, {
            color: '#FFB347',
            gradient1: 'rgba(255, 179, 71, 0.3)',
            gradient2: 'rgba(255, 179, 71, 0)',
            data: generateChartData(30, 20000, 25000, false)
        });
    }
}

// Chart.js 차트 생성
function createCryptoChart(canvas, options) {
    const ctx = canvas.getContext('2d');
    const gradient = ctx.createLinearGradient(0, 0, 0, canvas.height);
    gradient.addColorStop(0, options.gradient1);
    gradient.addColorStop(1, options.gradient2);
    
    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: options.data.labels,
            datasets: [{
                data: options.data.values,
                borderColor: options.color,
                backgroundColor: gradient,
                borderWidth: 2,
                fill: true,
                tension: 0.4,
                pointRadius: 0,
                pointHoverRadius: 4,
                pointHoverBackgroundColor: options.color,
                pointHoverBorderColor: '#fff',
                pointHoverBorderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    enabled: true,
                    mode: 'index',
                    intersect: false,
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        label: function(context) {
                            return '$' + context.parsed.y.toLocaleString();
                        }
                    }
                }
            },
            scales: {
                x: {
                    display: false
                },
                y: {
                    display: false
                }
            },
            interaction: {
                intersect: false,
                mode: 'index'
            }
        }
    });
}

// 간단한 SVG 차트 그리기 (Chart.js 대체)
function drawSimpleChart(canvasId, data) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    
    const parent = canvas.parentElement;
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('width', '100%');
    svg.setAttribute('height', '100%');
    svg.setAttribute('viewBox', '0 0 300 80');
    svg.style.display = 'block';
    
    // 데이터 정규화
    const values = data.values;
    const max = Math.max(...values);
    const min = Math.min(...values);
    const range = max - min;
    
    // 경로 생성
    let pathData = '';
    const width = 300;
    const height = 80;
    const step = width / (values.length - 1);
    
    values.forEach((value, index) => {
        const x = index * step;
        const y = height - ((value - min) / range) * height;
        
        if (index === 0) {
            pathData += `M ${x} ${y}`;
        } else {
            pathData += ` L ${x} ${y}`;
        }
    });
    
    // 그라데이션 정의
    const defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
    const gradient = document.createElementNS('http://www.w3.org/2000/svg', 'linearGradient');
    gradient.setAttribute('id', `gradient-${canvasId}`);
    gradient.setAttribute('x1', '0%');
    gradient.setAttribute('y1', '0%');
    gradient.setAttribute('x2', '0%');
    gradient.setAttribute('y2', '100%');
    
    const stop1 = document.createElementNS('http://www.w3.org/2000/svg', 'stop');
    stop1.setAttribute('offset', '0%');
    stop1.setAttribute('style', canvasId === 'ethereumChart' 
        ? 'stop-color:rgba(139, 159, 255, 0.4);stop-opacity:1' 
        : 'stop-color:rgba(255, 179, 71, 0.4);stop-opacity:1');
    
    const stop2 = document.createElementNS('http://www.w3.org/2000/svg', 'stop');
    stop2.setAttribute('offset', '100%');
    stop2.setAttribute('style', canvasId === 'ethereumChart' 
        ? 'stop-color:rgba(139, 159, 255, 0);stop-opacity:1' 
        : 'stop-color:rgba(255, 179, 71, 0);stop-opacity:1');
    
    gradient.appendChild(stop1);
    gradient.appendChild(stop2);
    defs.appendChild(gradient);
    svg.appendChild(defs);
    
    // 채우기 경로
    const fillPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    const fillData = pathData + ` L ${width} ${height} L 0 ${height} Z`;
    fillPath.setAttribute('d', fillData);
    fillPath.setAttribute('fill', `url(#gradient-${canvasId})`);
    svg.appendChild(fillPath);
    
    // 선 경로
    const linePath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    linePath.setAttribute('d', pathData);
    linePath.setAttribute('fill', 'none');
    linePath.setAttribute('stroke', canvasId === 'ethereumChart' ? '#8B9FFF' : '#FFB347');
    linePath.setAttribute('stroke-width', '2');
    svg.appendChild(linePath);
    
    // Canvas를 SVG로 교체
    canvas.style.display = 'none';
    parent.appendChild(svg);
}

// 차트 데이터 생성
function generateChartData(points, min, max, isIncreasing) {
    const labels = [];
    const values = [];
    
    let currentValue = (min + max) / 2;
    
    for (let i = 0; i < points; i++) {
        labels.push('');
        
        // 랜덤 변동
        const change = (Math.random() - 0.5) * (max - min) * 0.1;
        const trend = isIncreasing ? (max - min) * 0.02 : -(max - min) * 0.01;
        
        currentValue += change + trend;
        currentValue = Math.max(min, Math.min(max, currentValue));
        
        values.push(Math.round(currentValue));
    }
    
    return { labels, values };
}

// 랭크 이미지 업로드 기능
function initializeRankImageUpload() {
    const rankImageContainer = document.querySelector('.rank-image-container');
    const rankImage = document.getElementById('rankImage');
    
    if (rankImageContainer && rankImage) {
        // 클릭 시 파일 선택 다이얼로그 표시
        rankImageContainer.addEventListener('click', function() {
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = 'image/*';
            
            input.addEventListener('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    
                    reader.onload = function(e) {
                        rankImage.src = e.target.result;
                        window.showNotification('이미지가 업로드되었습니다.', 'success');
                    };
                    
                    reader.readAsDataURL(file);
                }
            });
            
            input.click();
        });
        
        // 드래그 앤 드롭 지원
        rankImageContainer.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.style.opacity = '0.7';
        });
        
        rankImageContainer.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.style.opacity = '1';
        });
        
        rankImageContainer.addEventListener('drop', function(e) {
            e.preventDefault();
            this.style.opacity = '1';
            
            const file = e.dataTransfer.files[0];
            if (file && file.type.startsWith('image/')) {
                const reader = new FileReader();
                
                reader.onload = function(e) {
                    rankImage.src = e.target.result;
                    window.showNotification('이미지가 업로드되었습니다.', 'success');
                };
                
                reader.readAsDataURL(file);
            }
        });
    }
}

// 미션 버튼 이벤트
function initializeMissionButton() {
    const missionButton = document.querySelector('.mission-button');
    
    if (missionButton) {
        missionButton.addEventListener('click', function() {
            // 미션 페이지로 이동 또는 모달 표시
            window.showNotification('미션 페이지로 이동합니다...', 'info');
            
            // 실제로는 다음과 같이 페이지 이동
            setTimeout(() => {
                // window.location.href = 'mission.jsp';
            }, 1000);
        });
    }
}

// 주식 테이블 인터랙션
function initializeStockTable() {
    const tableRows = document.querySelectorAll('.stock-row');
    let stockChart = null;
    
    // 주식 차트 데이터 (임시)
    const stockChartsData = {
        '삼성전자': generateChartData(30, 130000, 145000, false),
        'SK하이닉스': generateChartData(30, 680000, 750000, true),
        '두산에너빌리티': generateChartData(30, 80000, 90000, true),
        '현대모비스': generateChartData(30, 170000, 185000, true),
        'TSLL': generateChartData(30, 25000, 30000, false),
        '현대차': generateChartData(30, 295000, 315000, true)
    };
    
    // 초기 차트 생성 (삼성전자)
    const stockCanvas = document.getElementById('stockChart');
    if (stockCanvas) {
        if (typeof Chart !== 'undefined') {
            stockChart = createStockChart(stockCanvas, stockChartsData['삼성전자']);
        } else {
            drawSimpleStockChart('stockChart', stockChartsData['삼성전자']);
        }
    }
    
    // 첫 번째 행을 active로 설정
    if (tableRows.length > 0) {
        tableRows[0].classList.add('active');
    }
    
    // 각 행에 이벤트 리스너 추가
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', function() {
            // 모든 행의 active 제거
            tableRows.forEach(r => r.classList.remove('active'));
            
            // 현재 행을 active로 설정
            this.classList.add('active');
            
            // 종목명 가져오기
            const stockName = this.getAttribute('data-stock');
            
            // 차트 헤더 업데이트
            const chartStockName = document.querySelector('.chart-stock-name');
            if (chartStockName) {
                chartStockName.textContent = stockName;
            }
            
            // 차트 업데이트
            if (stockChart && stockChartsData[stockName]) {
                updateStockChart(stockChart, stockChartsData[stockName]);
            } else if (stockChartsData[stockName]) {
                // SVG 차트 업데이트
                drawSimpleStockChart('stockChart', stockChartsData[stockName]);
            }
        });
        
        row.addEventListener('click', function() {
            const stockName = this.getAttribute('data-stock');
            const price = this.querySelector('.price').textContent;
            
            console.log('선택된 종목:', stockName, price);
            // 상세 페이지로 이동 또는 모달 표시
        });
    });
}

// 주식 차트 생성
function createStockChart(canvas, data) {
    const ctx = canvas.getContext('2d');
    const gradient = ctx.createLinearGradient(0, 0, 0, canvas.height);
    gradient.addColorStop(0, 'rgba(59, 130, 246, 0.3)');
    gradient.addColorStop(1, 'rgba(59, 130, 246, 0)');
    
    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                data: data.values,
                borderColor: '#3B82F6',
                backgroundColor: gradient,
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointRadius: 0,
                pointHoverRadius: 6,
                pointHoverBackgroundColor: '#3B82F6',
                pointHoverBorderColor: '#fff',
                pointHoverBorderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    enabled: true,
                    mode: 'index',
                    intersect: false,
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        label: function(context) {
                            return context.parsed.y.toLocaleString() + '원';
                        }
                    }
                }
            },
            scales: {
                x: {
                    display: true,
                    grid: {
                        display: false
                    },
                    ticks: {
                        display: false
                    }
                },
                y: {
                    display: true,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)',
                        drawBorder: false
                    },
                    ticks: {
                        callback: function(value) {
                            return value.toLocaleString() + '원';
                        },
                        color: '#9CA3AF',
                        font: {
                            size: 11
                        }
                    }
                }
            },
            interaction: {
                intersect: false,
                mode: 'index'
            }
        }
    });
}

// 주식 차트 업데이트
function updateStockChart(chart, data) {
    chart.data.datasets[0].data = data.values;
    chart.update('active');
}

// 간단한 SVG 주식 차트 그리기
function drawSimpleStockChart(canvasId, data) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    
    const parent = canvas.parentElement;
    
    // 기존 SVG 제거
    const existingSvg = parent.querySelector('svg');
    if (existingSvg) {
        existingSvg.remove();
    }
    
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('width', '100%');
    svg.setAttribute('height', '100%');
    svg.setAttribute('viewBox', '0 0 600 400');
    svg.style.display = 'block';
    
    // 데이터 정규화
    const values = data.values;
    const max = Math.max(...values);
    const min = Math.min(...values);
    const range = max - min;
    
    // 경로 생성
    let pathData = '';
    const width = 600;
    const height = 400;
    const padding = 40;
    const chartWidth = width - padding * 2;
    const chartHeight = height - padding * 2;
    const step = chartWidth / (values.length - 1);
    
    values.forEach((value, index) => {
        const x = padding + index * step;
        const y = padding + chartHeight - ((value - min) / range) * chartHeight;
        
        if (index === 0) {
            pathData += `M ${x} ${y}`;
        } else {
            pathData += ` L ${x} ${y}`;
        }
    });
    
    // 그라데이션 정의
    const defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
    const gradient = document.createElementNS('http://www.w3.org/2000/svg', 'linearGradient');
    gradient.setAttribute('id', `stock-gradient-${canvasId}`);
    gradient.setAttribute('x1', '0%');
    gradient.setAttribute('y1', '0%');
    gradient.setAttribute('x2', '0%');
    gradient.setAttribute('y2', '100%');
    
    const stop1 = document.createElementNS('http://www.w3.org/2000/svg', 'stop');
    stop1.setAttribute('offset', '0%');
    stop1.setAttribute('style', 'stop-color:rgba(59, 130, 246, 0.3);stop-opacity:1');
    
    const stop2 = document.createElementNS('http://www.w3.org/2000/svg', 'stop');
    stop2.setAttribute('offset', '100%');
    stop2.setAttribute('style', 'stop-color:rgba(59, 130, 246, 0);stop-opacity:1');
    
    gradient.appendChild(stop1);
    gradient.appendChild(stop2);
    defs.appendChild(gradient);
    svg.appendChild(defs);
    
    // 그리드 라인
    for (let i = 0; i <= 5; i++) {
        const y = padding + (chartHeight / 5) * i;
        const gridLine = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        gridLine.setAttribute('x1', padding);
        gridLine.setAttribute('y1', y);
        gridLine.setAttribute('x2', width - padding);
        gridLine.setAttribute('y2', y);
        gridLine.setAttribute('stroke', 'rgba(0, 0, 0, 0.05)');
        gridLine.setAttribute('stroke-width', '1');
        svg.appendChild(gridLine);
    }
    
    // 채우기 경로
    const fillPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    const fillData = pathData + ` L ${width - padding} ${height - padding} L ${padding} ${height - padding} Z`;
    fillPath.setAttribute('d', fillData);
    fillPath.setAttribute('fill', `url(#stock-gradient-${canvasId})`);
    svg.appendChild(fillPath);
    
    // 선 경로
    const linePath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    linePath.setAttribute('d', pathData);
    linePath.setAttribute('fill', 'none');
    linePath.setAttribute('stroke', '#3B82F6');
    linePath.setAttribute('stroke-width', '3');
    svg.appendChild(linePath);
    
    // Canvas 숨기기
    canvas.style.display = 'none';
    parent.appendChild(svg);
}

// 미니 차트 애니메이션
function animateMiniCharts() {
    const bars = document.querySelectorAll('.mini-bar-chart .bar');
    
    bars.forEach((bar, index) => {
        // 초기 높이 0으로 설정
        bar.style.height = '0';
        
        // 순차적으로 애니메이션
        setTimeout(() => {
            bar.style.height = `calc(${bar.style.getPropertyValue('--value')} * 1%)`;
        }, index * 50);
    });
}

// 실시간 데이터 업데이트 (선택사항)
function startRealtimeUpdates() {
    // 10초마다 데이터 업데이트
    setInterval(() => {
        updateCryptoPrices();
        updateStockPrices();
    }, 10000);
}

function updateCryptoPrices() {
    // 실제로는 API에서 데이터를 가져옴
    const cryptoCards = document.querySelectorAll('.crypto-card');
    
    cryptoCards.forEach(card => {
        const priceElement = card.querySelector('.crypto-price');
        const changeValueElement = card.querySelector('.change-value');
        const changePercentElement = card.querySelector('.change-percent');
        
        if (priceElement) {
            // 가격 업데이트 시뮬레이션
            const currentPrice = parseFloat(priceElement.textContent.replace(/[$,]/g, ''));
            const change = (Math.random() - 0.5) * 100;
            const newPrice = currentPrice + change;
            
            priceElement.textContent = window.Utils.formatPrice(Math.round(newPrice));
            
            // 변화량 업데이트
            if (changeValueElement && changePercentElement) {
                const isPositive = change > 0;
                changeValueElement.textContent = (isPositive ? '+' : '') + window.Utils.formatPrice(Math.abs(Math.round(change)));
                changeValueElement.className = 'change-value ' + (isPositive ? 'positive' : 'negative');
                
                const percent = (change / currentPrice) * 100;
                changePercentElement.textContent = window.Utils.formatPercent(percent);
                changePercentElement.className = 'change-percent ' + (isPositive ? 'positive' : 'negative');
            }
        }
    });
}

function updateStockPrices() {
    // 주식 가격 업데이트 로직
    const rows = document.querySelectorAll('.stock-table tbody tr');
    
    rows.forEach(row => {
        const priceElement = row.querySelector('.price');
        const changeElement = row.querySelector('.change');
        
        if (priceElement && Math.random() > 0.7) { // 30% 확률로 업데이트
            // 가격 변경 애니메이션
            row.style.backgroundColor = 'rgba(59, 130, 246, 0.05)';
            
            setTimeout(() => {
                row.style.backgroundColor = '';
            }, 500);
        }
    });
}

// 데이터 새로고침 함수
function refreshData() {
    window.showNotification('데이터를 새로고침하는 중...', 'info');
    
    // 차트 데이터 업데이트
    if (ethereumChart) {
        const newData = generateChartData(30, 20000, 25000, true);
        ethereumChart.data.datasets[0].data = newData.values;
        ethereumChart.update('none');
    }
    
    if (bitcoinChart) {
        const newData = generateChartData(30, 20000, 25000, false);
        bitcoinChart.data.datasets[0].data = newData.values;
        bitcoinChart.update('none');
    }
    
    // 가격 데이터 업데이트
    updateCryptoPrices();
    updateStockPrices();
    
    setTimeout(() => {
        window.showNotification('데이터가 업데이트되었습니다.', 'success');
    }, 1000);
}

// 전역에서 사용 가능하도록 export
window.refreshData = refreshData;
