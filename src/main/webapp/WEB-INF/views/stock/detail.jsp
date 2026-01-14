<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>종목 상세 페이지</title>
<%@ include file="/WEB-INF/views/common/common.jsp"%>
<link rel="stylesheet" href="${cpath}/resources/css/detail/detail.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
<link rel="stylesheet"
	href="${cpath}/resources/css/detail/community.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/common.css">
</head>
<body class="detail-body">
	<div class="detail-container">
		<%@ include file="../common/sidebar.jsp"%>
		<main class="detail-main-content">
			<%@ include file="../common/header.jsp"%>
			<div class="detail-stock-container">
				<div class="detail-left-panel">
					<section class="detail-stock-header">
						<img src="${cpath}/resources/images/icon/samsung.png" alt="삼성전자">
						<div class="detail-text-col">
							<div>
								<h2 class="detail-stock-name">
									삼성전자 <span class="stock-code">005930</span>
								</h2>
							</div>
							<div class="detail-current-price">
								<h3>129,300원</h3>
							</div>
						</div>
						<div class="detail-star-icon">
							<img src="${cpath}/resources/images/icon/star.png" alt="즐겨찾기">
						</div>
					</section>

					<section class="detail-chart-area"></section>
					<section class="detail-bottom-split">
						<section class="detail-hoga-box">
							<div class="detail-strong">
								<h3>호가</h3>
							</div>
						</section>
						<section class="detail-community-box">
							<div class="detail-strong">
								<h3>커뮤니티</h3>
							</div>

							<!-- 커뮤니티 내용 include -->
							<%@ include file="/WEB-INF/views/stock/community.jsp"%>

						</section>
					</section>
				</div>
				<aside class="detail-right-panel">
					<div class="detail-order-top">
						<h3>주문하기</h3>
						<span class="detail-change-link">주문 방법 바꾸기</span>
					</div>
					<div class="detail-tab-group">
						<button class="detail-tab-active">매수</button>
						<button class="detail-tab">매도</button>
						<button class="detail-tab">대기</button>
					</div>
					<div class="detail-input-card">
						<div class="detail-row">
							<span class="detail-card-label"> <span
								class="detail-active-type">지정가</span> | <span>시장가</span>
							</span> <span class="detail-big-price">129,300원</span>
						</div>
						<div class="detail-row">
							<span>수량</span> <span class="detail-big-price">10주</span>
						</div>
					</div>

					<div class="detail-order-result">
						<div class="detail-row">
							<span>구매 가능 금액</span> <span class="detail-big-price"
								class="detail-money">1,293,000원</span>
						</div>
						<div class="detail-row">
							<span>내 주식 평균</span> <span class="detail-big-price"
								class="detail-money">129,300원</span>
						</div>
						<div class="detail-total-row">
							<span>주문금액</span> <span class="detail-big-price"
								class="detail-total-money">1,293,000원</span>
						</div>
					</div>
					<div class="detail-sentiment-section">
						<div class="detail-sentiment-text">
							🔥 현재 투자자 78%가 <span class="detail-red-text">매수</span>쪽으로 몰려요!
						</div>
						<div class="detail-percent-labels">
							<div style="width: 78%;">78%</div>
							<div style="width: 22%;">22%</div>
						</div>
						<div>
							<div class="detail-progress-bar">
								<div class="detail-fill-buy" style="width: 78%;"></div>
								<div class="detail-fill-sell" style="width: 22%;"></div>
							</div>
						</div>
					</div>
					<button class="detail-btn-submit">매수</button>
				</aside>
			</div>
		</main>
	</div>
</body>
</html>
