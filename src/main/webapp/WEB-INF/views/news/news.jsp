<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 뉴스</title>
    <%@ include file="/WEB-INF/views/common/common.jsp" %>
    <link rel="stylesheet" href="${cpath}/resources/css/news/news.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
</head>
<body>
    <div class="news-app-container">
        <%@ include file="../common/sidebar.jsp"%>
        <%@ include file="../common/header.jsp"%>
        <main class="news-main-content">
            <!-- 진행률 카드 -->
            <div class="news-progress-card">
                <div class="news-progress-header">
                    <div class="news-progress-left">
                        <h2 class="news-progress-title">오늘의 증권 뉴스 달성률</h2>
                        <p class="news-progress-subtitle">뉴스를 클릭하면 해당 뉴스로 이동합니다.</p>
                    </div>
                    <div class="news-progress-status" id="news-progressStatus">0% 달성!</div>
                </div>
                <div class="news-progress-bar-container">
                    <div class="news-progress-bar" id="news-progressBar" style="width: 0%"></div>
                </div>
            </div>
            <div class="news-section-card">
                <h2 class="news-section-title">오늘의 증권 뉴스</h2>
                <!-- 뉴스 카드 컨테이너 -->
                <div class="news-cards-container" id="newsContainer">
                    <!-- 로딩 상태 -->
                    <div class="news-loading">
                        <div class="news-loading-spinner"></div>
                        <p class="news-loading-text">뉴스를 불러오는 중...</p>
                    </div>
                </div>
            </div>
        </main>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
            const cpath = "${pageContext.request.contextPath}";
        </script>
        <script src="${cpath}/resources/js/news/news.js"></script>
    </div>
</body>
</html>
