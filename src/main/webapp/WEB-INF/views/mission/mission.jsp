<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>똑똑한개미 - 미션</title>
<%@ include file="/WEB-INF/views/common/common.jsp"%>
<link rel="stylesheet" href="${cpath}/resources/css/mission/mission.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
</head>
<body>
	<div class="mission-app-container">
		<%@ include file="../common/sidebar.jsp"%>
		<%@ include file="../common/header.jsp"%>
		<main class="mission-main-content">
			<!-- 진행률 카드 -->
			<div class="mission-progress-card">
				<div class="mission-progress-header">
					<h2 class="mission-progress-title">오늘의 미션 달성률</h2>
					<div class="mission-progress-status" id="mission-progressStatus">0%
						달성!</div>
				</div>
				<div class="mission-progress-bar-container">
					<div class="mission-progress-bar" id="mission-progressBar"
						style="width: 0%"></div>
				</div>
			</div>

        <!-- 미션 카드 래퍼 -->
        <div class="mission-cards-wrapper">
            <!-- 미션 카드 컨테이너 -->
            <div class="mission-cards-container">
                <!-- 미션 1: 경제 퀴즈 풀기 -->
                <div class="mission-card" id="mission-card-quiz">
                    <div class="mission-card-header">
                        <h3 class="mission-card-title">미션 1. 오늘의 경제 퀴즈 풀기</h3>
                    </div>
                    <div class="mission-card-body">
                        <p class="mission-card-description">매일 2문제, 정답을 맞히면 <b>문제당 100P!</b><br>
                            경제 지식도 쌓고 나의 개미 랭크도 높여보세요!</p>
                    </div>
                    <div class="mission-card-footer">
                        <button class="mission-card-btn" id="quiz-start-btn" onclick="location.href='${cpath}/mission/quiz'">도전하기</button>
                    </div>
                </div>

                <!-- 미션 2: 경제 뉴스 읽기 -->
                <div class="mission-card" id="mission-card-news">
                    <div class="mission-card-header">
                        <h3 class="mission-card-title">미션 2. 오늘의 증권 뉴스 읽기</h3>
                    </div>
                    <div class="mission-card-body">
                        <p class="mission-card-description">최신 증권 뉴스를 확인하고 <b>기사당 20P</b> 획득!<br>
                            하루 5번, 시장의 흐름을 읽으면 포인트가 쌓입니다.</p>
                    </div>
                    <div class="mission-card-footer">
                        <button class="mission-card-btn" id="news-start-btn" onclick="location.href='${cpath}/news'">도전하기</button>
                    </div>
                </div>
            </div>

            <!-- 랭크 시스템 링크 -->
				<div class="mission-rank-link-wrapper">
                <button class="mission-rank-link-btn" id="rank-system-btn">랭크 시스템 보러가기</button>
            </div>
        </div>
    </main>

    <!-- 랭크 시스템 모달 -->
    <div class="mission-modal" id="rank-modal">
        <div class="mission-modal-content" style="max-width: 700px;">
            <div class="mission-modal-header">
                <div class="mission-ant-ranks-system">개미 랭크 시스템</div>
                <button class="mission-modal-close" id="rank-modal-close">×</button>
            </div>
            <div class="mission-modal-body">
                <div class="mission-completion-screen active">
                    <div class="mission-ant-ranks">
                        <div class="mission-rank-item">
                            <img id="mission-rank-img-브론즈" src="${cpath}/resources/images/profile/bronze-ant.png" alt="브론즈" />
                            <div class="mission-rank-label" data-rank="브론즈">브론즈 개미</div>
                        </div>
                        <div class="mission-rank-item">
                            <img id="mission-rank-img-실버" src="${cpath}/resources/images/profile/silver-ant.png" alt="실버" />
                            <div class="mission-rank-label" data-rank="실버">실버 개미</div>
                        </div>
                        <div class="mission-rank-item">
                            <img id="mission-rank-img-골드" src="${cpath}/resources/images/profile/gold-ant.png" alt="골드" />
                            <div class="mission-rank-label" data-rank="골드">골드 개미</div>
                        </div>
                        <div class="mission-rank-item">
                            <img id="mission-rank-img-다이아몬드" src="${cpath}/resources/images/profile/diamond-ant.png" alt="다이아몬드" />
                            <div class="mission-rank-label" data-rank="다이아몬드">다이아몬드 개미</div>
                        </div>
                        <div class="mission-rank-item">
                            <img id="mission-rank-img-챌린저" src="${cpath}/resources/images/profile/challenger-ant.png" alt="챌린저" />
                            <div class="mission-rank-label" data-rank="챌린저">챌린저 개미</div>
                        </div>
                    </div>
                    <div class="mission-reward-info">
                        <div class="mission-next-level" id="mission-reward-message">
                            다음 랭크까지 <strong id="mission-points-needed">0 P</strong>
                        </div>
                        <div class="mission-points-progress">
                            <div class="mission-points-bar" id="mission-reward-progressBar" style="width: 0%"></div>
                            <span class="mission-points-label" id="mission-reward-currentRankStartPoint">0 P</span>
                            <span class="mission-points-label" id="mission-reward-currentPoint">0 P</span>
                            <span class="mission-points-label" id="mission-reward-nextRankPoint">0 P</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        const cpath = "${pageContext.request.contextPath}";
        let currentUserId = null;
        <c:if test="${not empty pageContext.request.userPrincipal}">
        currentUserId = parseInt('${pageContext.request.userPrincipal.name}');
        </c:if>
    </script>
    <script src="${cpath}/resources/js/mission/mission.js"></script>
</div>
</body>
</html>