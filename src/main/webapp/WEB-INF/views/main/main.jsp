<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/common.jsp" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 메인</title>
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/main/main.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700;900&family=Poppins:wght@500;600;700&display=swap"
          rel="stylesheet">
</head>
<body>
<div class="app-container">
    <!-- 사이드바 include -->
    <%@ include file="../common/sidebar.jsp" %>

    <!-- 메인 콘텐츠 영역 -->
    <main class="main-content">
        <!-- 헤더 include -->
        <%@ include file="../common/header.jsp" %>

        <!-- 콘텐츠 그리드 -->
        <div class="content-grid">
            <!-- 나의 랭크 카드 -->
            <div class="rank-card grid-rank">
                <h2 class="card-title">나의 랭크</h2>
                <div class="rank-image-container">
                    <!-- 랭크 이미지를 추가할 수 있는 영역 -->
                    <img src="${cpath}/resources/image/rank/rank_bronze.png" alt="랭크 이미지" class="rank-image" id="rankImage">
                    <div class="rank-image-placeholder">
                        <svg width="80" height="80" viewBox="0 0 80 80" fill="none">
                            <path d="M40 10L50 30H70L54 42L60 62L40 50L20 62L26 42L10 30H30L40 10Z" fill="#E0E0E0"/>
                        </svg>
                        <p>이미지를 추가하세요</p>
                    </div>
                </div>
            </div>

            <!-- 코스피 카드 -->
            <div class="crypto-card grid-kospi">
                <div class="crypto-header">
                    <div class="crypto-icon ethereum">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <path d="M12 2L4 12.5L12 16.5L20 12.5L12 2Z" fill="white"/>
                            <path d="M12 18L4 14L12 22L20 14L12 18Z" fill="white" opacity="0.6"/>
                        </svg>
                    </div>
                    <div class="crypto-info">
                        <h3 class="crypto-name">코스피</h3>
                        <p class="crypto-symbol">Ethereum</p>
                    </div>
                </div>
                <div class="crypto-price">$23,738</div>
                <div class="crypto-change">
                    <span class="change-label">PNL Daily</span>
                    <span class="change-value positive">+$189.91</span>
                    <span class="change-percent positive">+24.68%</span>
                </div>
                <div class="crypto-chart">
                    <canvas id="ethereumChart"></canvas>
                </div>
            </div>


            <!-- 미션 카드 -->
            <div class="mission-card grid-mission">
                <div class="mission-header">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <rect x="3" y="3" width="4" height="4" rx="1" fill="#FF3366"/>
                        <rect x="10" y="3" width="4" height="4" rx="1" fill="#FF3366"/>
                        <rect x="17" y="3" width="4" height="4" rx="1" fill="#FF3366"/>
                    </svg>
                    <h3>미션 : 경제 퀴즈 풀기</h3>
                </div>
                <p class="mission-description">OX 퀴즈 맞히고 100P 받아가세요!</p>
                <button class="mission-button">도전하기</button>
            </div>

            <!-- 코스닥 카드 -->
            <div class="crypto-card grid-kosdaq">
                <div class="crypto-header">
                    <div class="crypto-icon bitcoin">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <circle cx="12" cy="12" r="10" fill="white"/>
                            <path d="M13.5 8.5V7H11.5V8.5H10V7H8V8.5H6.5V10H7.5V14H6.5V15.5H8V17H10V15.5H11.5V17H13.5V15.5H15C15.8284 15.5 16.5 14.8284 16.5 14V13C16.5 12.4477 16.1642 11.9822 15.6667 11.7929C16.0597 11.5527 16.3333 11.1095 16.3333 10.6V10C16.3333 9.17157 15.6618 8.5 14.8333 8.5H13.5Z"
                                  fill="#FF9500"/>
                        </svg>
                    </div>
                    <div class="crypto-info">
                        <h3 class="crypto-name">코스닥</h3>
                        <p class="crypto-symbol">Bitcoin</p>
                    </div>
                </div>
                <div class="crypto-price">$23,738</div>
                <div class="crypto-change">
                    <span class="change-label">PNL Daily</span>
                    <span class="change-value negative">-$16.78</span>
                    <span class="change-percent positive">+14.67%</span>
                </div>
                <div class="crypto-chart">
                    <canvas id="bitcoinChart"></canvas>
                </div>
            </div>

            <!-- 주식 테이블과 차트 영역 -->
            <div class="stock-section grid-table">
                <!-- 주식 테이블 -->
                <div class="stock-table-card">
                    <div class="table-wrapper">
                        <table class="stock-table">
                            <thead>
                            <tr>
                                <th class="rank-company" colspan="2">2026.01.11 12:45 기준 거래량 순위</th>
                                <th class="price">현재가</th>
                                <th class="change">등락률</th>
                                <th class="chart-mini">매수/매도 비율</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="stock-row" data-stock="삼성전자">
                                <td class="rank">1</td>
                                <td class="company">
                                    <div class="company-logo samsung"></div>
                                    <span>삼성전자</span>
                                </td>
                                <td class="price">137,200원</td>
                                <td class="change negative">-0.65%</td>
                                <td class="chart-mini">
                                    <div class="mini-bar-chart">
                                        <span class="bar" style="--value: 60; --color: #4A90E2"></span>
                                        <span class="bar" style="--value: 40; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="stock-row" data-stock="SK하이닉스">
                                <td class="rank">2</td>
                                <td class="company">
                                    <div class="company-logo sk"></div>
                                    <span>SK하이닉스</span>
                                </td>
                                <td class="price">717,000원</td>
                                <td class="change positive">+3.01%</td>
                                <td class="chart-mini">
                                    <div class="mini-bar-chart">
                                        <span class="bar" style="--value: 57; --color: #4A90E2"></span>
                                        <span class="bar" style="--value: 43; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="stock-row" data-stock="두산에너빌리티">
                                <td class="rank">3</td>
                                <td class="company">
                                    <div class="company-logo doosan"></div>
                                    <span>두산에너빌리티</span>
                                </td>
                                <td class="price">85,100원</td>
                                <td class="change positive">+2.28%</td>
                                <td class="chart-mini">
                                    <div class="mini-bar-chart">
                                        <span class="bar" style="--value: 70; --color: #4A90E2"></span>
                                        <span class="bar" style="--value: 30; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="stock-row" data-stock="현대모비스">
                                <td class="rank">4</td>
                                <td class="company">
                                    <div class="company-logo hyundai"></div>
                                    <span>현대모비스</span>
                                </td>
                                <td class="price">179,000원</td>
                                <td class="change positive">+6.99%</td>
                                <td class="chart-mini">
                                    <div class="mini-bar-chart">
                                        <span class="bar" style="--value: 58; --color: #4A90E2"></span>
                                        <span class="bar" style="--value: 42; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="stock-row" data-stock="TSLL">
                                <td class="rank">5</td>
                                <td class="company">
                                    <div class="company-logo tsll"></div>
                                    <span>TSLL</span>
                                </td>
                                <td class="price">27,764원</td>
                                <td class="change negative">-0.15%</td>
                                <td class="chart-mini">
                                    <div class="mini-bar-chart">
                                        <span class="bar" style="--value: 49; --color: #4A90E2"></span>
                                        <span class="bar" style="--value: 51; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="stock-row" data-stock="현대차">
                                <td class="rank">6</td>
                                <td class="company">
                                    <div class="company-logo hyundai-motor"></div>
                                    <span>현대차</span>
                                </td>
                                <td class="price">307,000원</td>
                                <td class="change positive">+0.82%</td>
                                <td class="chart-mini">
                                    <div class="mini-bar-chart">
                                        <span class="bar" style="--value: 16; --color: #4A90E2"></span>
                                        <span class="bar" style="--value: 84; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                
                <!-- 주식 차트 -->
                <div class="stock-chart-card">
                    <div class="chart-header">
                        <h3 class="chart-stock-name">삼성전자</h3>
                        <p class="chart-stock-code">005930</p>
                    </div>
                    <div class="stock-chart-container">
                        <canvas id="stockChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>
<script>
    // 페이지 로드 시 스크롤을 맨 위로 이동
    window.onload = function() {
        window.scrollTo(0, 0);
    };

    // 페이지 진입 시 즉시 스크롤 초기화 (더 빠른 실행)
    if (history.scrollRestoration) {
        history.scrollRestoration = 'manual';
    }
</script>
<script src="${cpath}/resources/js/main/main.js"></script>
</body>
</html>
