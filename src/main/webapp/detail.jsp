<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<%@ include file="/WEB-INF/views/common/common.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>종목 상세 페이지</title>
<link rel="stylesheet" type="text/css"
	href="${cpath}/resources/css/detail.css">
<link rel="stylesheet" type="text/css"
	href="${cpath}/resources/css/sidebar.css">
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/sidebar.jsp" />
	<main>
		<div class="stock-container">
			<div class="left-panel">
				<section class="stock-header">
					<img src="resources/images/icon/samsung.png" alt="삼성전자">
					<div class="text-col">
						<div class="name-row">
							<h2 class="stock-name">
								삼성전자 <span class="stock-code">005930</span>
							</h2>
						</div>
						<div class="price-row">
							<div class="current-price">129,300원</div>
						</div>
					</div>
					<div class="star-icon">
						<img src="resources/images/icon/star.png" alt="즐겨찾기">
					</div>
				</section>

				<section class="chart-area"></section>
				<section class="bottom-split">
					<section class="hoga-box">
						<div class="strong">
							<h3>호가</h3>
						</div>
					</section>
					<section class="community-box">
						<div class="strong">
							<h3>커뮤니티</h3>
						</div>
					</section>
				</section>

			</div>
			<aside class="right-panel">
				<div class="order-top">
					<h3>주문하기</h3>
					<span class="change-link">주문 방법 바꾸기</span>
				</div>
				<div class="tab-group">
					<button class="tab active">매수</button>
					<button class="tab">매도</button>
					<button class="tab">대기</button>
				</div>

				<div class="order-inputs">
					<div class="input-card">
						<div class="row">
							<span class="card-label"> <span class="active-type">지정가</span>
								| <span class="inactive-type">시장가</span>
							</span> <span class="big-price">129,300원</span>
						</div>
						<div class="row">
							<span>수량</span> <span class="big-price">10주</span>
						</div>
					</div>
				</div>

				<div class="order-result">
					<div class="row">
						<span>구매 가능 금액</span> <span class="big-price" class="money">1,293,000원</span>
					</div>
					<div class="row">
						<span>내 주식 평균</span> <span class="big-price" class="money">129,300원</span>
					</div>
					<div class="row total-row">
						<span>주문금액</span> <span class="big-price" class="total-money">1,293,000원</span>
					</div>
				</div>
				<div class="sentiment-section">
					<div class="sentiment-text">
						🔥 현재 투자자 78%가 <span class="red-text">매수</span>쪽으로 몰려요!
					</div>
					<div class="percent-labels">
						<div class="label-buy" style="width: 78%;">78%</div>
						<div class="label-sell" style="width: 22%;">22%</div>
					</div>

					<div>

						<div class="progress-bar">
							<div class="fill-buy" style="width: 78%;"></div>
							<div class="fill-sell" style="width: 22%;"></div>
						</div>
					</div>
				</div>
				<button class="btn-submit">매수</button>
			</aside>

		</div>
	</main>
</body>
</html>
