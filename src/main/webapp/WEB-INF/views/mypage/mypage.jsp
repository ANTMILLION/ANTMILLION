<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 마이페이지</title>
    <%@ include file="/WEB-INF/views/common/common.jsp" %>
    <link rel="stylesheet" href="${cpath}/resources/css/mypage/mypage.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
</head>
<body>
<div class="mypage-app-container">
    <!-- 사이드바 include -->
    <%@ include file="../common/sidebar.jsp" %>

    <!-- 메인 콘텐츠 영역 -->
    <main class="mypage-main-content">
        <!-- 헤더 include -->
        <%@ include file="../common/header.jsp" %>

        <!-- 콘텐츠 영역 -->
        <div class="mypage-content-wrapper">
            <!-- 좌측 영역 -->
            <div class="mypage-left-section">
                <!-- 탭 메뉴 -->
                <div class="mypage-tab-menu">
                    <button class="mypage-tab-button active" data-tab="stock">주식잔고</button>
                    <button class="mypage-tab-button" data-tab="realized">실현손익</button>
                    <button class="mypage-tab-button" data-tab="executed">체결내역</button>
                    <button class="mypage-tab-button" data-tab="trading">매매내역</button>
                </div>

                <!-- 탭 콘텐츠 -->
                <div class="mypage-tab-content">
                    <!-- 주식잔고 탭 -->
                    <div class="mypage-tab-panel active" id="stock-panel">
                        <h2 class="mypage-section-title">주식잔고</h2>
                        
                        <!-- 주식 카드 목록 -->
                        <div class="mypage-stock-list" id="stockList">
                            <!-- JavaScript로 동적 생성 -->
                        </div>
                    </div>

                    <!-- 실현손익 탭 -->
                    <div class="mypage-tab-panel" id="realized-panel">
                        <h2 class="mypage-section-title">실현손익</h2>
                        
                        <!-- 실현손익 목록 -->
                        <div class="mypage-realized-list" id="realizedList">
                            <!-- JavaScript로 동적 생성 -->
                        </div>
                    </div>

                    <!-- 체결내역 탭 -->
                    <div class="mypage-tab-panel" id="executed-panel">
                        <h2 class="mypage-section-title">체결내역</h2>
                        
                        <!-- 서브 탭 -->
                        <div class="mypage-sub-tab-menu">
                            <button class="mypage-sub-tab-button active" data-subtab="all">전체</button>
                            <button class="mypage-sub-tab-button" data-subtab="executed">체결</button>
                            <button class="mypage-sub-tab-button" data-subtab="unexecuted">미체결</button>
                        </div>

                        <!-- 체결내역 테이블 -->
                        <div class="mypage-table-wrapper">
                            <table class="mypage-executed-table" id="executedTable">
                                <thead>
                                    <tr>
                                        <th>종목명</th>
                                        <th>주문수량</th>
                                        <th>체결수량</th>
                                        <th>미체결수량</th>
                                        <th>미체결금액</th>
                                        <th>주문시간</th>
                                    </tr>
                                    <tr>
                                        <th>매매구분</th>
                                        <th>주문단가</th>
                                        <th>체결단가</th>
                                        <th>주문금액</th>
                                        <th>종목코드</th>
                                        <th>체결시간</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- JavaScript로 동적 생성 -->
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- 매매내역 탭 -->
                    <div class="mypage-tab-panel" id="trading-panel">
                        <h2 class="mypage-section-title">매매내역</h2>
                        
                        <!-- 필터 버튼 -->
                        <div class="mypage-filter-menu">
                            <button class="mypage-filter-button active" data-filter="all">전체</button>
                            <button class="mypage-filter-button" data-filter="buy">매수</button>
                            <button class="mypage-filter-button" data-filter="sell">매도</button>
                        </div>

                        <!-- 매매내역 목록 -->
                        <div class="mypage-trading-list" id="tradingList">
                            <!-- JavaScript로 동적 생성 -->
                        </div>
                    </div>
                </div>
            </div>

            <!-- 우측 영역 -->
            <div class="mypage-right-section">
                <!-- 총 자산 카드 -->
                <div class="mypage-asset-card">
                    <h3 class="mypage-asset-title">내 자산 총액</h3>
                    <div class="mypage-asset-amount" id="totalAsset">2,833,000원</div>
                    <div class="mypage-asset-profit">
                        <span class="mypage-profit-label">총 평가 손익</span>
                        <span class="mypage-profit-amount positive" id="totalProfit">276,000원(+27.14%)</span>
                    </div>
                </div>

                <!-- 심리경고 카드 -->
                <div class="mypage-alert-card">
                    <div class="mypage-alert-header">
                        <h3 class="mypage-alert-title">심리경고</h3>
                        <select class="mypage-alert-filter" id="alertFilter">
                            <option value="all">전체</option>
                            <option value="sell">매몰비용 오류</option>
                            <option value="buy">손실회피</option>
                        </select>
                    </div>
                    
                    <div class="mypage-alert-content">
                        <h4 class="mypage-alert-section-title">매몰비용 오류 내역</h4>
                        <p class="mypage-alert-description">
                            종목의 주세가 하향(이동평균선 역배열 등)인데, 계속해서 추가 매수를 진행하여
                            비중이 지나치게 커지고 있음
                        </p>
                        <p class="mypage-alert-tip">
                            단순히 매입 단가를 낮추기 위한 매몰비용 오류에 빠진 것은 아닌가요?
                        </p>

                        <!-- 알림 목록 -->
                        <div class="mypage-alert-list" id="alertList">
                            <!-- JavaScript로 동적 생성 -->
                        </div>
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

    // 페이지 진입 시 즉시 스크롤 초기화
    if (history.scrollRestoration) {
        history.scrollRestoration = 'manual';
    }
</script>
<script src="${cpath}/resources/js/mypage/mypage.js"></script>
</body>
</html>
