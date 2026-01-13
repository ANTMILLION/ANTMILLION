<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 종목</title>
    <%@ include file="/WEB-INF/views/common/common.jsp" %>
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/stocklist/stocklist.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
</head>
<body>
<div class="app-container">
    <!-- 사이드바 include -->
    <%@ include file="../common/sidebar.jsp" %>

    <!-- 메인 콘텐츠 영역 -->
    <main class="main-content">
        <!-- 헤더 include -->
        <%@ include file="../common/header.jsp" %>

        <!-- 종목 리스트 컨텐츠 -->
        <div class="stocklist-container">
            <!-- 탭 버튼 -->
            <div class="stocklist-tabs">
                <button class="stocklist-tab-btn active" data-tab="all">전체종목</button>
                <button class="stocklist-tab-btn" data-tab="favorite">관심종목</button>
            </div>

            <!-- 업데이트 시간 -->
            <div class="stocklist-update-time">
                <span class="stocklist-label">순위·오늘 09:27 기준</span>
                <div class="stocklist-info-group">
                    <span class="stocklist-info-label">현재가</span>
                    <span class="stocklist-info-label">등락률</span>
                    <span class="stocklist-info-label">매수/매도 비율</span>
                </div>
            </div>

            <!-- 종목 리스트 -->
            <div class="stocklist-items" id="stocklist-all">
                <!-- JavaScript로 동적 생성 -->
            </div>

            <!-- 관심종목 리스트 (초기에는 숨김) -->
            <div class="stocklist-items hidden" id="stocklist-favorite">
                <!-- JavaScript로 동적 생성 -->
            </div>
        </div>
    </main>
</div>
<script src="${cpath}/resources/js/stocklist/stocklist.js"></script>
</body>
</html>
