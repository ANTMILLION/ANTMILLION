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
                <div class="main-rank-header">
                    <h2 class="main-card-title">${userRank.nickName}님의 랭크</h2>
                    <div class="main-rank-image-container">
                        <img src="${cpath}/${userRank.rankImage}" alt="랭크 이미지" class="main-rank-image" id="main-rankImage">
                    </div>
                </div>
                <div class="main-exp-bar-container"
                     id="mainExpContainer"
                     data-current="${userRank.currentPoint}"
                     data-max="${userRank.nextRankPoint}"
                     data-start="${userRank.currentRankStartPoint}">

                    <div class="main-exp-bar" id="mainExpBar" style="width: 0%;"></div>

                    <div class="main-exp-text">
                        <span class="main-exp-current">${userRank.currentPoint}</span>
                        <span class="main-exp-divider">/</span>
                        <c:choose>
                            <c:when test="${userRank.nextRankPoint == 0 || userRank.nextRankPoint >= 10000}">
                                <span class="main-exp-max">MAX</span>
                            </c:when>
                            <c:otherwise>
                                <span class="main-exp-max">${userRank.nextRankPoint}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- 코스피 카드 -->
            <div class="main-market-index-card main-grid-kospi">
                <div class="main-market-index-header">
                    <div class="main-market-index-info">
                        <h3 class="main-market-index-name">코스피</h3>
                        <p class="main-market-index-symbol">KOSPI</p>
                    </div>
                </div>
                <div class="main-market-index-price">현재 수치</div>
                <div class="main-market-index-change">
                    <span class="main-change-label">전일대비</span>
                    <span class="main-change-value main-positive">전일대비 금액</span>
                    <span class="main-change-percent main-positive">전일대비 등락률</span>
                </div>
                <div class="main-market-index-chart" id="main-kospi-chart"></div>
            </div>


            <!-- 미션 카드 -->
            <div class="main-mission-card main-grid-mission">
                <div class="main-mission-header">
                    <h3>미션 : 오늘의 경제 퀴즈 풀기</h3>
                </div>
                <p class="main-mission-description">OX 퀴즈 맞히고 100P 받아가세요!</p>
                <a href="${cpath}/mission" class="main-mission-button">도전하기</a>
            </div>

            <!-- 코스닥 카드 -->
            <div class="main-market-index-card main-grid-kosdaq">
                <div class="main-market-index-header">
                    <div class="main-market-index-info">
                        <h3 class="main-market-index-name">코스닥</h3>
                        <p class="main-market-index-symbol">KOSDAQ</p>
                    </div>
                </div>
                <div class="main-market-index-price">현재 수치</div>
                <div class="main-market-index-change">
                    <span class="main-change-label">전일대비</span>
                    <span class="main-change-value main-positive">전일대비 금액</span>
                    <span class="main-change-percent main-positive">전일대비 등락률</span>
                </div>
                <div class="main-market-index-chart" id="main-kosdaq-chart"></div>
            </div>

            <!-- 주식 테이블과 차트 영역 -->
            <div class="main-stock-section main-grid-table">
                <!-- 주식 테이블 -->
                <div class="main-stock-table-card">
                    <div class="main-stocklist-header">
                        <div class="main-stocklist-time">거래량 순위·오늘 09:27 기준</div>
                    </div>
                    <div class="main-stocklist-table-header">
                        <div class="main-stocklist-header-cell"></div>
                        <div class="main-stocklist-header-cell">종목명</div>
                        <div class="main-stocklist-header-cell">현재가</div>
                        <div class="main-stocklist-header-cell">등락률</div>
                        <div class="main-stocklist-header-cell">거래 비율</div>
                    </div>
                    <div class="main-stocklist-list" id="main-stocklist-Container">
                        <!-- JavaScript로 동적 생성 -->
                    </div>
                </div>
                
                <!-- 주식 차트 -->
                <div class="main-stock-chart-card">
                    <div class="main-chart-header">
                        <h3 class="main-chart-stock-name"></h3>
                        <p class="main-chart-stock-code"></p>
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
    document.addEventListener('DOMContentLoaded', function() {
        // HTML 태그에서 데이터 가져오기
        const container = document.getElementById('mainExpContainer');
        const progressBar = document.getElementById('mainExpBar');

        if (container && progressBar) {
            // data- 속성은 문자열이므로 숫자로 변환
            const currentPoint = parseInt(container.getAttribute('data-current')) || 0;
            const nextRankPoint = parseInt(container.getAttribute('data-max')) || 0;
            const startPoint = parseInt(container.getAttribute('data-start')) || 0;

            // 미션 페이지와 동일한 계산 로직 적용
            let percent = 0;

            // 최고 레벨(nextRankPoint가 0이거나 10000 이상)인지 확인
            if (nextRankPoint > 0 && nextRankPoint < 10000) {
                const rangeTotal = nextRankPoint - startPoint;   // 이번 랭크의 전체 구간
                const rangeCurrent = currentPoint - startPoint;  // 내가 달성한 구간

                if (rangeTotal > 0) {
                    percent = (rangeCurrent / rangeTotal) * 100;
                }
            } else {
                percent = 100;
            }

            // 0 ~ 100% 사이로 제한
            percent = Math.max(0, Math.min(100, percent));

            setTimeout(() => {
                progressBar.style.width = percent + "%";
            }, 100);
        }
    });


    // 페이지 로드 시 스크롤을 맨 위로 이동
    window.onload = function() {
        window.scrollTo(0, 0);
    };

    // 페이지 진입 시 즉시 스크롤 초기화 (더 빠른 실행)
    if (history.scrollRestoration) {
        history.scrollRestoration = 'manual';
    }
</script>
<!-- Adding the standalone version of Lightweight charts -->
<script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
<script> const contextPath = '${cpath}'; </script>
<script src="${cpath}/resources/js/main/main.js"></script>
</body>
</html>
