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

// 손실회피 체크 함수
async function checkLossAversionAlert(stockCode) {
    console.log('[손실회피 API 호출] stockCode:', stockCode);
    
    try {
        const url = contextPath + '/api/bias-alert/check-loss-aversion?stockCode=' + stockCode;
        console.log('[손실회피 API URL]', url);
        
        const response = await fetch(url);
        console.log('[손실회피 API 응답 상태]', response.status);
        
        if (response.status === 204) {
            console.log('[결과] 보유하지 않은 종목');
            return null;
        }
        
        if (!response.ok) {
            throw new Error('API 호출 실패: ' + response.status);
        }
        
        const data = await response.json();
        console.log('[손실회피 API 결과]', data);
        return data;
        
    } catch (error) {
        console.error('[손실회피 API 에러]', error);
        return null;
    }
}

// 매몰비용오류 체크 함수
async function checkSunkCostAlert(stockCode) {
    console.log('[매몰비용오류 API 호출] stockCode:', stockCode);
    
    try {
        const url = contextPath + '/api/bias-alert/check-sunk-cost?stockCode=' + stockCode;
        console.log('[매몰비용오류 API URL]', url);
        
        const response = await fetch(url);
        console.log('[매몰비용오류 API 응답 상태]', response.status);
        
        if (response.status === 204) {
            console.log('[결과] 보유하지 않은 종목');
            return null;
        }
        
        if (!response.ok) {
            throw new Error('매몰비용오류 API 호출 실패: ' + response.status);
        }
        
        const data = await response.json();
        console.log('[매몰비용오류 API 결과]', data);
        return data;
        
    } catch (error) {
        console.error('[매몰비용오류 API 에러]', error);
        return null;
    }
}

// FOMO 체크 함수
async function checkFomoAlert(stockCode) {
    console.log('[FOMO API 호출] stockCode:', stockCode);
    
    try {
        const url = contextPath + '/api/bias-alert/check-fomo?stockCode=' + stockCode;
        console.log('[FOMO API URL]', url);
        
        const response = await fetch(url);
        console.log('[FOMO API 응답 상태]', response.status);
        
        if (response.status === 204) {
            console.log('[결과] 조회 실패');
            return null;
        }
        
        if (!response.ok) {
            throw new Error('FOMO API 호출 실패: ' + response.status);
        }
        
        const data = await response.json();
        console.log('[FOMO API 결과]', data);
        return data;
        
    } catch (error) {
        console.error('[FOMO API 에러]', error);
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

        const currentPrice = document.getElementById("detail-current-price-h3");
        currentPrice.innerText = Number(data[data.length-1].stck_prpr).toLocaleString('ko-KR') + '원';

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

// 날짜 포맷 변경 (main.js의 formatDate 함수와 동일)
function formatDate(yyyymmdd) {
    return {
        year: Number(yyyymmdd.substring(0, 4)),
        month: Number(yyyymmdd.substring(4, 6)),
        day: Number(yyyymmdd.substring(6, 8)),
    };
}

// 일/주/월/년봉 차트 그리기 함수
function drawPeriodChart(stockCode, period) {
    const chartContainer = document.getElementById('detail-stockChart');
    if (!chartContainer) {
        console.error("#detail-stockChart 요소를 찾을 수 없음");
        return;
    }

    chartContainer.innerHTML = ''; // 기존 차트 삭제

    const width = chartContainer.clientWidth;
    const height = chartContainer.clientHeight;

    stockChart = LightweightCharts.createChart(chartContainer, {
        width: width,
        height: height,
        layout: {
            background: {type: 'solid', color: 'white'},
            textColor: 'black'
        },
        grid: {
            vertLines: { color: '#eee' },
            horzLines: { color: '#eee' }
        },
        timeScale: {
            borderColor: '#cccccc',
            timeVisible: false,
            secondsVisible: false
        }
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
    fetch(`/antmillion/api/kis/periodChart/${stockCode}?period=${period}`)
        .then(res => res.json())
        .then(data => {
            if (!data || data.length === 0) {
                console.error("데이터가 비어있음");
                return;
            }

            // 데이터 정렬
            data.sort((a, b) => a.stck_bsop_date.localeCompare(b.stck_bsop_date));

            // 차트 데이터 변환
            const chartData = data.map(row => ({
                time: formatDate(row.stck_bsop_date),
                open: Number(row.stck_oprc),
                high: Number(row.stck_hgpr),
                low: Number(row.stck_lwpr),
                close: Number(row.stck_clpr)
            }));

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

        // 차트 기간 버튼 이벤트 리스너 추가
        const periodButtons = document.querySelectorAll('.detail-period-btn');
        periodButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                // 모든 버튼에서 active 클래스 제거
                periodButtons.forEach(b => b.classList.remove('detail-period-active'));

                // 클릭된 버튼에 active 클래스 추가
                this.classList.add('detail-period-active');

                const period = this.getAttribute('data-period');

                if (period === 'minute') {
                    // 분봉 차트
                    drawDetailChart(stockCode);
                } else {
                    // 일/주/월/년봉 차트
                    drawPeriodChart(stockCode, period);
                }
            });
        });
    } else {
        console.error("URL에 종목 코드가 없습니다.");
    }
});

// 반응형 대응
window.addEventListener('resize', () => {
    if (stockChart) {
        const container = document.querySelector('#detail-stockChart');
        stockChart.resize(container.clientWidth, container.clientHeight);
    }
});

// ===== DOM 로드 후 실행 =====
document.addEventListener('DOMContentLoaded', async function() {
    checkFavoriteStatus();
    console.log('=== 매매 편향 체크 시스템 로드 완료 (API 연동) ===');
    
    // ===== 페이지 진입 시 편향 체크 (우선순위: 매몰비용 > 손실회피) =====
    try {
        const urlParams = new URLSearchParams(window.location.search);
        const stockCode = urlParams.get('code');
        
        if (stockCode) {
            console.log('[페이지 로드] 편향 체크 시작 - stockCode:', stockCode);
            
            // 우선순위 1: 매몰비용오류
            const sunkCostData = await checkSunkCostAlert(stockCode);
            
            // 우선순위 2: 손실회피
            const lossData = await checkLossAversionAlert(stockCode);
            
            // 우선순위 4: FOMO
            const fomoData = await checkFomoAlert(stockCode);
            
            // 우선순위에 따라 표시 (매몰비용 > 손실회피 > FOMO)
            if (sunkCostData && sunkCostData.hasAlert) {
                console.log('[우선순위 1] 매몰비용오류 경고: ' + sunkCostData.stockName + ' ' + sunkCostData.profitRate + '% 손실, ' + sunkCostData.holdingDays + '일 보유');
                
                if (typeof showBiasAlert === 'function') {
                    showBiasAlert('SUNK_COST');
                    
                    if (typeof checkUnreadAlerts === 'function') {
                        setTimeout(() => { checkUnreadAlerts(); }, 0);
                    }
                }
            } else if (lossData && lossData.hasAlert) {
                console.log('[우선순위 2] 손실회피 경고: ' + lossData.stockName + ' ' + lossData.profitRate + '% 손실 중');
                
                if (typeof showBiasAlert === 'function') {
                    showBiasAlert('LOSS_AVERSION');
                    
                    if (typeof checkUnreadAlerts === 'function') {
                        setTimeout(() => { checkUnreadAlerts(); }, 0);
                    }
                }
            } else if (fomoData && fomoData.hasAlert) {
                console.log('[우선순위 4] FOMO 경고: ' + fomoData.stockName + ' +' + fomoData.profitRate + '% 급등');
                
                if (typeof showBiasAlert === 'function') {
                    showBiasAlert('FOMO');
                    
                    if (typeof checkUnreadAlerts === 'function') {
                        setTimeout(() => { checkUnreadAlerts(); }, 0);
                    }
                }
            } else {
                console.log('[페이지 로드] 편향 조건 미달');
            }
        }
    } catch (error) {
        console.error('[페이지 로드] 편향 체크 에러:', error);
    }
    
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
                
                // 매수 탭 클릭 시 편향 체크 (우선순위: 매몰비용 > 손실회피 > FOMO)
                const urlParams = new URLSearchParams(window.location.search);
                const stockCode = urlParams.get('code');
                console.log('[종목 코드]', stockCode);
                
                if (stockCode) {
                    try {
                        // 우선순위 1: 매몰비용
                        const sunkCostData = await checkSunkCostAlert(stockCode);
                        
                        // 우선순위 2: 손실회피
                        const lossData = await checkLossAversionAlert(stockCode);
                        
                        // 우선순위 4: FOMO
                        const fomoData = await checkFomoAlert(stockCode);
                        
                        if (sunkCostData && sunkCostData.hasAlert) {
                            console.log('[매수 탭] 매몰비용 경고: ' + sunkCostData.stockName + ' ' + sunkCostData.profitRate + '% 손실, ' + sunkCostData.holdingDays + '일 보유');
                            
                            if (typeof showBiasAlert === 'function') {
                                showBiasAlert('SUNK_COST');
                                
                                if (typeof checkUnreadAlerts === 'function') {
                                    setTimeout(() => { checkUnreadAlerts(); }, 0);
                                }
                            }
                        } else if (lossData && lossData.hasAlert) {
                            console.log('[매수 탭] 손실회피 경고: ' + lossData.stockName + ' ' + lossData.profitRate + '% 손실 중');
                            
                            if (typeof showBiasAlert === 'function') {
                                showBiasAlert('LOSS_AVERSION');
                                
                                if (typeof checkUnreadAlerts === 'function') {
                                    setTimeout(() => { checkUnreadAlerts(); }, 0);
                                }
                            }
                        } else if (fomoData && fomoData.hasAlert) {
                            console.log('[매수 탭] FOMO 경고: ' + fomoData.stockName + ' +' + fomoData.profitRate + '% 급등');
                            
                            if (typeof showBiasAlert === 'function') {
                                showBiasAlert('FOMO');
                                
                                if (typeof checkUnreadAlerts === 'function') {
                                    setTimeout(() => { checkUnreadAlerts(); }, 0);
                                }
                            }
                        } else {
                            console.log('[매수 탭] 편향 조건 미달');
                        }
                    } catch (error) {
                        console.error('[매수 탭] 편향 체크 에러:', error);
                    }
                } else {
                    console.warn('URL에 종목 코드(code) 없음');
                }
                   
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
                
                // 매도 탭 클릭 시 위험회피 체크
                const urlParams2 = new URLSearchParams(window.location.search);
                const stockCode2 = urlParams2.get('code');
                console.log('[종목 코드]', stockCode2);
                
                if (stockCode2) {
                    try {
                        const riskData = await checkBiasAlert(stockCode2);
                        
                        if (riskData && riskData.hasAlert) {
                            console.log('위험회피 경고: ' + riskData.stockName + ' +' + riskData.profitRate + '% 수익 중 (' + riskData.holdingDays + '일 보유)');
                            
                            if (typeof showBiasAlert === 'function') {
                                showBiasAlert('RISK_AVERSION');
                                
                                if (typeof checkUnreadAlerts === 'function') {
                                    setTimeout(() => { checkUnreadAlerts(); }, 0);
                                }
                            }
                        } else {
                            // 위험회피 조건 미달
                            if (riskData) {
                                console.log('위험회피 조건 미달 - 수익률: ' + riskData.profitRate + '%, 보유일수: ' + riskData.holdingDays + '일');
                            }
                            // 손실회피 경고는 유지하므로 hideBiasAlert() 호출 안 함
                            console.log('[경고] 기존 경고 유지 (손실회피 경고가 있을 수 있음)');
                        }
                    } catch (error) {
                        console.error('[매도 탭] 위험회피 체크 에러:', error);
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