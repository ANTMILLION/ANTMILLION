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
					<div class="mission-quiz-question" id="mission-quizQuestion"></div>
					<div class="mission-quiz-options" id="mission-quizOptions"></div>
				</div>

				<!-- 완료 화면 -->
				<div class="mission-completion-screen" id="mission-completionScreen">
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
						<h3 id="mission-reward-rankName"></h3>
					</div>
					<div class="mission-reward-info">
						<div class="mission-points-progress">
							<div class="mission-points-bar" id="mission-reward-progressBar" style="width: 0%"></div>
							<span class="mission-points-label" id="mission-reward-currentRankStartPoint">0 P</span>
							<span class="mission-points-label" id="mission-reward-currentPoint">0 P</span>
							<span class="mission-points-label" id="mission-reward-nextRankPoint">0 P</span>
						</div>
						<div class="mission-next-level" id="mission-reward-message"></div>
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
			let currentUserId = null;
			<c:if test="${not empty pageContext.request.userPrincipal}">
			currentUserId = parseInt('${pageContext.request.userPrincipal.name}');
			</c:if>
		</script>
		<script src="${cpath}/resources/js/mission/mission.js"></script>
	</div>
</body>
</html>