<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 미션</title>
    <%@ include file="/WEB-INF/views/common/common.jsp" %>
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
    <link rel="stylesheet" href="${cpath}/resources/css/mission/mission.css">
</head>
<body>
<div class="app-container">
    <%@ include file="../common/sidebar.jsp" %>

    <%@ include file="../common/header.jsp" %>

    <main class="main-content">
        <!-- 진행률 카드 -->
        <div class="progress-card">
            <div class="progress-header">
                <h2 class="progress-title">오늘의 미션 달성률</h2>
                <div class="progress-status" id="progressStatus">0% 달성!</div>
            </div>
            <div class="progress-bar-container">
                <div class="progress-bar" id="progressBar" style="width: 0%"></div>
            </div>
        </div>

        <!-- 퀴즈 카드 -->
        <div class="quiz-card" id="quizCard">
            <div class="quiz-header">
                <h3 class="quiz-date">1월 2일 금요일 경제 퀴즈</h3>
                <!-- 포인트 메시지 -->
                <div class="points-message" id="pointsMessage" style="display: none;">
                    100 포인트 획득하였습니다.
                </div>
            </div>
            <div id="quizContent">
                <div class="quiz-question" id="quizQuestion">
                    위험을 줄이기 위해 자산을 여러 곳에 나누어 투자하는 원칙을 분산투자라고 한다.
                </div>
                <div class="quiz-options" id="quizOptions">
                    <!-- 동적으로 생성됨 -->
                </div>
            </div>

            <!-- 완료 화면 -->
            <div class="completion-screen" id="completionScreen">
                <div class="ant-ranks">
                    <img src="${cpath}/resources/images/profile/bronze-ant.png" alt="브론즈"/>
                    <img src="${cpath}/resources/images/profile/silver-ant.png" alt="실버"/>
                    <img src="${cpath}/resources/images/profile/gold-ant.png" alt="골드"/>
                    <img src="${cpath}/resources/images/profile/diamond-ant.png" alt="다이아"/>
                    <img src="${cpath}/resources/images/profile/challenger-ant.png" alt="챌린저"/>
                </div>
                <div class="reward-info">
                    <div class="points-progress">
                        <div class="points-bar"></div>
                        <span class="points-label">680포인트</span>
                        <span class="points-label">1000포인트</span>
                    </div>
                    <div class="next-level">다음 랭크까지 320포인트가 남았습니다.</div>
                </div>
            </div>
        </div>
    </main>

    <!-- 오답 모달 -->
    <div class="modal" id="wrongModal">
        <div class="modal-content">
            <button class="modal-close" onclick="closeModal()">✕</button>
            <h2 class="modal-title">다시 한번 생각해보세요.</h2>
            <p class="modal-message">퀴즈를 맞히고 똑똑한개미 랭크를 높여보세요!</p>
        </div>
    </div>

    <script src="${cpath}/resources/js/mission/mission.js"></script>
</div>
</body>
</html>