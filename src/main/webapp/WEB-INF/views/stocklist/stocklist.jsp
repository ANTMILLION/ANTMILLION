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
<div class="stocklist-app-container">
    <%@ include file="../common/sidebar.jsp" %>
    <%@ include file="../common/header.jsp" %>

    <main class="stocklist-main-content">
        <div class="stocklist-card">
            <div class="stocklist-tabs">
                <button class="stocklist-tab-btn active">전체종목</button>
                <button class="stocklist-tab-btn">관심종목</button>
            </div>
            <div class="stocklist-header">
                <div class="stocklist-time">거래량 순위·오늘 09:27 기준</div>
                <div class="stocklist-view-options">
                    <span class="stocklist-help-icon">?</span>
                </div>
            </div>
            <div class="stocklist-table-header">
                <div class="stocklist-header-cell"></div>
                <div class="stocklist-header-cell">종목명</div>
                <div class="stocklist-header-cell">현재가</div>
                <div class="stocklist-header-cell">등락률</div>
                <div class="stocklist-header-cell">거래 비율</div>
            </div>

            <!-- 종목 리스트 -->
            <div class="stocklist-list" id="stocklist-Container">
            </div>
        </div>
    </main>
</div>
<script>
    // contextPath 변수 설정 (stocklist.js에서 사용)
    var contextPath = '${cpath}';
</script>
<script src="${cpath}/resources/js/stocklist/stocklist.js"></script>
</body>
</html>