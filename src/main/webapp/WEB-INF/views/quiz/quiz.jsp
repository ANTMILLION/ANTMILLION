<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>똑똑한개미 - 퀴즈</title>
<%@ include file="/WEB-INF/views/common/common.jsp"%>
<link rel="stylesheet" href="${cpath}/resources/css/mission/quiz.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
</head>
<body>
	<div class="quiz-app-container">
		<%@ include file="../common/sidebar.jsp"%>
		<%@ include file="../common/header.jsp"%>
		<main class="quiz-main-content">
			<!-- 진행률 카드 -->
			<div class="quiz-progress-card">
				<div class="quiz-progress-header">
					<h2 class="quiz-progress-title">오늘의 경제 퀴즈 달성률</h2>
					<div class="quiz-progress-status" id="quiz-progressStatus">0%
						달성!</div>
				</div>
				<div class="quiz-progress-bar-container">
					<div class="quiz-progress-bar" id="quiz-progressBar"
						style="width: 0%"></div>
				</div>
			</div>

			<!-- 퀴즈 카드 -->
			<div class="quiz-quiz-card" id="quiz-quizCard">
				<div class="quiz-quiz-header">
					<h3 class="quiz-quiz-date"></h3>
				</div>
				<div id="quiz-quizContent">
					<div class="quiz-quiz-question" id="quiz-quizQuestion"></div>
					<div class="quiz-quiz-options" id="quiz-quizOptions"></div>
				</div>

				<!-- 완료 화면 -->
				<div class="quiz-completion-screen" id="quiz-completionScreen">
					<div class="quiz-completion-message">
						<div class="quiz-completion-icon">🎉</div>
						<br>오늘의 경제 퀴즈 풀기 미션을 성공하였습니다.<br>축하드립니다!
					</div>
					<button class="quiz-back-to-mission-btn" onclick="location.href='${cpath}/mission'">
						미션으로 돌아가기
					</button>
				</div>
			</div>
		</main>

		<!-- 결과(정답/오답) 모달 -->
		<div class="quiz-modal" id="quiz-result-Modal">
			<div class="quiz-modal-content">
				<div class="quiz-modal-header">
					<button class="quiz-modal-close" onclick="closeModal()">✕</button>
				</div>
				<div class="quiz-modal-body">
					<!-- 포인트 메시지 -->
					<div class="quiz-points-message" id="quiz-pointsMessage" style="display: none;"></div>
					<h2 class="quiz-modal-title"></h2>
					<p class="quiz-modal-message"></p>
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
		<script src="${cpath}/resources/js/quiz/quiz.js"></script>
	</div>
</body>
</html>