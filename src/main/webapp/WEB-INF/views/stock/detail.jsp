<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>종목 상세 페이지</title>
<%@ include file="/WEB-INF/views/common/common.jsp"%>
<link rel="stylesheet" href="${cpath}/resources/css/stock/detail.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
<link rel="stylesheet" href="${cpath}/resources/css/stock/community.css">
</head>
<body class="detail-body">
    <div class="detail-container">
        <%@ include file="../common/sidebar.jsp"%>
        <main class="detail-main-content">
            <%@ include file="../common/header.jsp"%>
            <div class="detail-stock-container">
                <div class="detail-left-panel">
                    <section class="detail-stock-header">
                        <img src="" alt="삼성전자">
                        <div class="detail-text-col">
                            <div>
                                <h2 class="detail-stock-name">
                                    ${stock.stockName} <span class="stock-code">${stock.stockCode}</span>
                                </h2>
                            </div>
                            <div class="detail-current-price">
                                <h3 id="detail-current-price-h3"></h3>
                            </div>
                        </div>
                        <button class="detail-favorite-btn" data-code="${stock.stockCode}">
                            ♡
                        </button>
                    </section>

                    <section class="detail-chart-area">
                        <!-- 차트 기간 선택 버튼 추가 -->
                        <div class="detail-chart-period-buttons">
                            <button class="detail-period-btn detail-period-active" data-period="minute">분</button>
                            <button class="detail-period-btn" data-period="D">일</button>
                            <button class="detail-period-btn" data-period="W">주</button>
                            <button class="detail-period-btn" data-period="M">월</button>
                            <button class="detail-period-btn" data-period="Y">년</button>
                        </div>
					    <div id="detail-stockChart" style="width: 100%; height: 280px;"></div>
					</section>
                    <section class="detail-bottom-split">
                        <section class="detail-hoga-box">
                        </section>
                        <section class="detail-community-box">
                            <div class="detail-strong">
                                <h3>커뮤니티</h3>
                            </div>

                            <!-- 커뮤니티 내용 include -->
                            <%@ include file="/WEB-INF/views/stock/community.jsp"%>

                        </section>
                    </section>
                </div>
                <aside class="detail-right-panel">
                    <div class="detail-order-top">
                        <h3>주문하기</h3>
                        <span class="detail-change-link">주문 방법 바꾸기</span>
                    </div>
                    <div class="detail-tab-group">
                        <button class="detail-tab-active" data-type="buy">매수</button>
                        <button class="detail-tab" data-type="sell">매도</button>
                        <button class="detail-tab" data-type="pending">대기</button>
                    </div>
                    <div class="detail-input-card">
                        <div class="detail-row">
                            <span class="detail-card-label"> 
                                <span id="tab-limit" class="detail-price-type detail-active-type" style="cursor:pointer;">지정가</span> | 
                                <span id="tab-market" class="detail-price-type" style="cursor:pointer; color:#6B7280;">시장가</span>
                            </span> 
                            <span id="order-display-price" class="detail-big-price">129,300원</span>
                        </div>
                        <div class="detail-row">
                            <span>수량</span> <span class="detail-big-price">10주</span>
                        </div>
                    </div>

                    <div class="detail-order-result">
                        <div class="detail-row">
                            <span id="available-label">구매 가능 금액</span> <span class="detail-big-price"
                                class="detail-money">1,293,000원</span>
                        </div>
                        <div class="detail-row">
                            <span>내 주식 평균</span> <span class="detail-big-price"
                                class="detail-money">129,300원</span>
                        </div>
                        <div class="detail-total-row">
                            <span>주문금액</span> <span class="detail-big-price"
                                class="detail-total-money" id="total-money">1,293,000원</span>
                        </div>
                    </div>
                    <div class="detail-sentiment-section">
                        <div class="detail-sentiment-text">
                            🔥 현재 투자자 <span id="sentiment-percent">78</span>%가 <span class="detail-red-text" id="sentiment-direction">매수</span>쪽으로 몰려요!
                        </div>
                        <div class="detail-percent-labels">
                            <div style="width: 78%;" id="buy-percent">78%</div>
                            <div style="width: 22%;" id="sell-percent">22%</div>
                        </div>
                        <div>
                            <div class="detail-progress-bar">
                                <div class="detail-fill-buy" style="width: 78%;" id="buy-bar"></div>
                                <div class="detail-fill-sell" style="width: 22%;" id="sell-bar"></div>
                            </div>
                        </div>
                    </div>
                    <button class="detail-btn-submit" id="submit-btn">매수</button>
                </aside>
            </div>
        </main>
    </div>
    
<!-- SockJS 라이브러리 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>

<!-- STOMP 라이브러리 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<!-- Adding the standalone version of Lightweight charts -->
<script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
<script src="${cpath}/resources/js/stock/detail.js"></script>
</body>
</html>
