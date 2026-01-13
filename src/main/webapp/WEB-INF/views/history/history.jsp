<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 히스토리</title>
    <%@ include file="/WEB-INF/views/common/common.jsp" %>
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
    <link rel="stylesheet" href="${cpath}/resources/css/history/history.css">
</head>
<body>
<div class="history-app-container">
    <%@ include file="../common/sidebar.jsp" %>
    <%@ include file="../common/header.jsp" %>

	<main class="main-content">
        <div class="history-container">
            <section class="filter-box">
                <div class="filter-group-header">카테고리 별 조회</div>
                <div class="filter-controls">
                    <select class="select-custom">
                        <option value="all">전체</option>
                        <option value="sunk-cost" selected>매몰비용 오류</option>
                        <option value="loss-aversion">손실회피</option>
                    </select>
                    <div class="history-search-wrapper">
                        <input type="text" class="history-search-bar" placeholder="종목을 검색하세요">
                    </div>
                </div>
            </section>

            <section class="content-card">
                <div class="card-header">
                    <h2 class="history-title">매몰비용 오류 내역</h2>
                    <p class="history-text">
                        신한지주 종목의 추세가 하향(이동평균선 역배열 등)인데, 계속해서 추가 매수를 진행하여 비중이 지나치게 커지고 있음.<br>
                        단순히 매입 단가를 낮추기 위한 매몰 비용 오류에 빠진 것은 아닌가요?
                    </p>
                </div>

                <div class="history-list">
                    <span class="date-label">2026.01.02</span>
                    <div class="stock-detail-card">
                        <div class="stock-info-left">
                            <span class="stock-name">신한지주</span>
                            <div class="trade-info">
                                <span class="buy-tag">매수</span>
                                <span class="amount-text">10주</span>
                            </div>
                        </div>
                        <div class="stock-info-right">
                            <div class="price-main">700,000원</div>
                            <div class="price-change">(-10%)</div>
                        </div>
                    </div>
                </div>
                </section>
        </div>
    </main>



    <script src="${cpath}/resources/js/history/history.js"></script>
</div>
</body>
</html>