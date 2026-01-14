<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 메인</title>
    <%@ include file="/WEB-INF/views/common/common.jsp" %>
    <link rel="stylesheet" href="${cpath}/resources/css/main/main.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/common.css">
</head>
<body>
<div class="main-app-container">
    <!-- 사이드바 include -->
    <%@ include file="../common/sidebar.jsp" %>

    <!-- 메인 콘텐츠 영역 -->
    <main class="main-main-content">
        <!-- 헤더 include -->
        <%@ include file="../common/header.jsp" %>

        <!-- 콘텐츠 그리드 -->
        <div class="main-content-grid">
            <!-- 나의 랭크 카드 -->
            <div class="main-rank-card main-grid-rank">
                <h2 class="main-card-title">나의 랭크</h2>
                <div class="main-rank-image-container">
                    <!-- 랭크 이미지를 추가할 수 있는 영역 -->
                    <img src="${cpath}/resources/images/profile/gold-ant.png" alt="랭크 이미지" class="main-rank-image" id="main-rankImage">
                </div>
                <!-- 경험치 바 -->
                <div class="main-exp-bar-container">
                    <div class="main-exp-bar" style="width: 68%;"></div>
                    <div class="main-exp-text">
                        <span class="main-exp-current">680</span>
                        <span class="main-exp-divider">/</span>
                        <span class="main-exp-max">1000</span>
                    </div>
                </div>
            </div>

            <!-- 코스피 카드 -->
            <div class="main-crypto-card main-grid-kospi">
                <div class="main-crypto-header">
                    <div class="main-crypto-icon main-ethereum">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <path d="M12 2L4 12.5L12 16.5L20 12.5L12 2Z" fill="white"/>
                            <path d="M12 18L4 14L12 22L20 14L12 18Z" fill="white" opacity="0.6"/>
                        </svg>
                    </div>
                    <div class="main-crypto-info">
                        <h3 class="main-crypto-name">코스피</h3>
                        <p class="main-crypto-symbol">Ethereum</p>
                    </div>
                </div>
                <div class="main-crypto-price">$23,738</div>
                <div class="main-crypto-change">
                    <span class="main-change-label">PNL Daily</span>
                    <span class="main-change-value main-positive">+$189.91</span>
                    <span class="main-change-percent main-positive">+24.68%</span>
                </div>
                <div class="main-crypto-chart">
                    <canvas id="main-ethereumChart"></canvas>
                </div>
            </div>


            <!-- 미션 카드 -->
            <div class="main-mission-card main-grid-mission">
                <div class="main-mission-header">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <rect x="3" y="3" width="4" height="4" rx="1" fill="#FF3366"/>
                        <rect x="10" y="3" width="4" height="4" rx="1" fill="#FF3366"/>
                        <rect x="17" y="3" width="4" height="4" rx="1" fill="#FF3366"/>
                    </svg>
                    <h3>미션 : 경제 퀴즈 풀기</h3>
                </div>
                <p class="main-mission-description">OX 퀴즈 맞히고 100P 받아가세요!</p>
                <button class="main-mission-button">도전하기</button>
            </div>

            <!-- 코스닥 카드 -->
            <div class="main-crypto-card main-grid-kosdaq">
                <div class="main-crypto-header">
                    <div class="main-crypto-icon main-bitcoin">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                            <circle cx="12" cy="12" r="10" fill="white"/>
                            <path d="M13.5 8.5V7H11.5V8.5H10V7H8V8.5H6.5V10H7.5V14H6.5V15.5H8V17H10V15.5H11.5V17H13.5V15.5H15C15.8284 15.5 16.5 14.8284 16.5 14V13C16.5 12.4477 16.1642 11.9822 15.6667 11.7929C16.0597 11.5527 16.3333 11.1095 16.3333 10.6V10C16.3333 9.17157 15.6618 8.5 14.8333 8.5H13.5Z"
                                  fill="#FF9500"/>
                        </svg>
                    </div>
                    <div class="main-crypto-info">
                        <h3 class="main-crypto-name">코스닥</h3>
                        <p class="main-crypto-symbol">Bitcoin</p>
                    </div>
                </div>
                <div class="main-crypto-price">$23,738</div>
                <div class="main-crypto-change">
                    <span class="main-change-label">PNL Daily</span>
                    <span class="main-change-value main-negative">-$16.78</span>
                    <span class="main-change-percent main-positive">+14.67%</span>
                </div>
                <div class="main-crypto-chart">
                    <canvas id="main-bitcoinChart"></canvas>
                </div>
            </div>

            <!-- 주식 테이블과 차트 영역 -->
            <div class="main-stock-section main-grid-table">
                <!-- 주식 테이블 -->
                <div class="main-stock-table-card">
                    <div class="main-table-wrapper">
                        <table class="main-stock-table">
                            <thead>
                            <tr>
                                <th class="main-rank-company" colspan="2">2026.01.11 12:45 기준 거래량 순위</th>
                                <th class="main-price">현재가</th>
                                <th class="main-change">등락률</th>
                                <th class="main-chart-mini">
										    <div class="tw2t-1apn5azd">
										        ??증권 거래 비율
										        <span class="main-_1kuojbl0">
										            <svg xmlns="http://www.w3.org/2000/svg" viewBox="143 -1757 2014 2014" style="width: 1em;">
										                <path d="M645 121.5Q414-14 278.5-245T143-750 278.5-1255 645-1621.5 1150-1757 1655-1621.5 2021.5-1255 2157-750 2021.5-245 1655 121.5 1150 257 645 121.5ZM1590 8.5Q1786-104 1898-303T2010-750 1898-1197 1590-1508.5 1150-1621 710-1508.5 402-1197 290-750 402-303 710 8.5 1150 121 1590 8.5ZM1078-761.5Q1102-801 1169-844 1233-881 1260.5-917.5T1288-1005Q1288-1061 1246-1099.5T1135-1138Q1068-1138 1023.5-1102T973-1008H820Q828-1081 872.5-1140.5T987-1234 1139-1268Q1227-1268 1295-1234T1401-1141 1439-1009Q1439-930 1403.5-870.5T1291-761Q1249-736 1230.5-720.5T1204.5-688 1197-642V-550H1054V-657Q1054-722 1078-761.5ZM1051.5-279.5Q1021-310 1021-354T1051.5-428.5 1125-459Q1169-459 1200-428.5T1231-354 1200-279.5 1125-249Q1082-249 1051.5-279.5Z" fill="currentColor"></path>
										            </svg>
										            
										            <div class="main-tooltip-content">
										                <div class="main-tooltip-title">??증권 투자자들의 구매·판매 체결비중을 나타내요</div>
										                <div class="main-tooltip-body">
										                    • 30분 이내 체결내역을 실시간으로 보여줘요.<br>
										                    • 투자자들의 심리와 수급추세를 알 수 있어요. 단, 시장 전체의 수급을 의미하지 않아요.
										                </div>
										            </div>
										        </span>
										    </div>
										</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="main-stock-row" data-stock="삼성전자">
                                <td class="main-rank">1</td>
                                <td class="main-company">
                                    <div class="main-company-logo main-samsung"></div>
                                    <span>삼성전자</span>
                                </td>
                                <td class="main-price">137,200원</td>
                                <td class="main-change main-negative">-0.65%</td>
                                <td class="main-chart-mini">
                                    <div class="main-mini-bar-chart">
                                        <span class="main-bar" style="--value: 60; --color: #4A90E2"></span>
                                        <span class="main-bar" style="--value: 40; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="main-stock-row" data-stock="SK하이닉스">
                                <td class="main-rank">2</td>
                                <td class="main-company">
                                    <div class="main-company-logo main-sk"></div>
                                    <span>SK하이닉스</span>
                                </td>
                                <td class="main-price">717,000원</td>
                                <td class="main-change main-positive">+3.01%</td>
                                <td class="main-chart-mini">
                                    <div class="main-mini-bar-chart">
                                        <span class="main-bar" style="--value: 57; --color: #4A90E2"></span>
                                        <span class="main-bar" style="--value: 43; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="main-stock-row" data-stock="두산에너빌리티">
                                <td class="main-rank">3</td>
                                <td class="main-company">
                                    <div class="main-company-logo main-doosan"></div>
                                    <span>두산에너빌리티</span>
                                </td>
                                <td class="main-price">85,100원</td>
                                <td class="main-change main-positive">+2.28%</td>
                                <td class="main-chart-mini">
                                    <div class="main-mini-bar-chart">
                                        <span class="main-bar" style="--value: 70; --color: #4A90E2"></span>
                                        <span class="main-bar" style="--value: 30; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="main-stock-row" data-stock="현대모비스">
                                <td class="main-rank">4</td>
                                <td class="main-company">
                                    <div class="main-company-logo main-hyundai"></div>
                                    <span>현대모비스</span>
                                </td>
                                <td class="main-price">179,000원</td>
                                <td class="main-change main-positive">+6.99%</td>
                                <td class="main-chart-mini">
                                    <div class="main-mini-bar-chart">
                                        <span class="main-bar" style="--value: 58; --color: #4A90E2"></span>
                                        <span class="main-bar" style="--value: 42; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="main-stock-row" data-stock="TSLL">
                                <td class="main-rank">5</td>
                                <td class="main-company">
                                    <div class="main-company-logo main-tsll"></div>
                                    <span>TSLL</span>
                                </td>
                                <td class="main-price">27,764원</td>
                                <td class="main-change main-negative">-0.15%</td>
                                <td class="main-chart-mini">
                                    <div class="main-mini-bar-chart">
                                        <span class="main-bar" style="--value: 49; --color: #4A90E2"></span>
                                        <span class="main-bar" style="--value: 51; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="main-stock-row" data-stock="현대차">
                                <td class="main-rank">6</td>
                                <td class="main-company">
                                    <div class="main-company-logo main-hyundai-motor"></div>
                                    <span>현대차</span>
                                </td>
                                <td class="main-price">307,000원</td>
                                <td class="main-change main-positive">+0.82%</td>
                                <td class="main-chart-mini">
                                    <div class="main-mini-bar-chart">
                                        <span class="main-bar" style="--value: 16; --color: #4A90E2"></span>
                                        <span class="main-bar" style="--value: 84; --color: #FF6B6B"></span>
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                
                <!-- 주식 차트 -->
                <div class="main-stock-chart-card">
                    <div class="main-chart-header">
                        <h3 class="main-chart-stock-name">삼성전자</h3>
                        <p class="main-chart-stock-code">005930</p>
                    </div>
                    <div class="main-stock-chart-container">
                        <canvas id="main-stockChart"></canvas>
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
