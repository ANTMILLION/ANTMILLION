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

			<!-- 퀴즈 카드 -->
			<div class="mission-quiz-card" id="mission-quizCard">
				<div class="mission-quiz-header">
					<h3 class="mission-quiz-date"></h3>
					<!-- 포인트 메시지 -->
					<div class="mission-points-message" id="mission-pointsMessage"
						style="display: none;">100 포인트 획득하였습니다.</div>
				</div>
				<div id="mission-quizContent">
					<div class="mission-quiz-question" id="mission-quizQuestion">
						위험을 줄이기 위해 자산을 여러 곳에 나누어 투자하는 원칙을 분산투자라고 한다.</div>
					<div class="mission-quiz-options" id="mission-quizOptions">
						<!-- 동적으로 생성됨 -->
					</div>
				</div>

				<!-- 완료 화면 -->
				<div class="mission-completion-screen" id="mission-completionScreen">
					<div class="mission-ant-ranks">
						<img src="${cpath}/resources/images/profile/bronze-ant.png"
							alt="브론즈" /> <img
							src="${cpath}/resources/images/profile/silver-ant.png" alt="실버" />
						<img src="${cpath}/resources/images/profile/gold-ant.png" alt="골드" />
						<img src="${cpath}/resources/images/profile/diamond-ant.png"
							alt="다이아" /> <img
							src="${cpath}/resources/images/profile/challenger-ant.png"
							alt="챌린저" />
					</div>
					<div class="mission-reward-info">
						<div class="mission-points-progress">
							<div class="mission-points-bar"></div>
							<span class="mission-points-label">680포인트</span> <span
								class="mission-points-label">1000포인트</span>
						</div>
						<div class="mission-next-level">다음 랭크까지 320포인트가 남았습니다.</div>
					</div>
				</div>
			</div>
		</main>

		<!-- 오답 모달 -->
		<div class="mission-modal" id="mission-wrongModal">
			<div class="mission-modal-content">
				<button class="mission-modal-close" onclick="closeModal()">✕</button>
				<h2 class="mission-modal-title">다시 한번 생각해보세요.</h2>
				<p class="mission-modal-message">퀴즈를 맞히고 똑똑한개미 랭크를 높여보세요!</p>
			</div>
		</div>
		<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
		<script>
			const cpath = "${pageContext.request.contextPath}";
		</script>
		<script src="${cpath}/resources/js/mission/mission.js"></script>
	</div>
</body>
</html>