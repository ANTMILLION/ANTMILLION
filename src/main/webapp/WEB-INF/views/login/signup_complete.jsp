<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="/WEB-INF/views/common/common.jsp"%>
<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>ANTMILLION</title>
<link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css" />
<link rel="stylesheet" href="${cpath}/resources/css/login/sign.css" />
</head>
<body>
	<div class="shell">
		<%@ include file="../common/sidebar.jsp"%>
		<div class="content">
			<div class="card">
				<h1 class="title-top">ANTMILLION</h1>
				<div class="hr"></div>

				<div class="complete">
					<div class="label">계좌번호</div>
					<div class="value">${accountNumber}</div>
				</div>

				<div class="complete" style="padding-top: 10px;">
					<div class="label">지급 금액</div>
					<div class="value">
						₩
						<fmt:formatNumber value="${balance}" type="number" />
					</div>
				</div>

				<div class="note">* 본 계좌는 모의투자용의 가상 계좌입니다.</div>

				<c:choose>
					<c:when test="${signupType eq 'KAKAO'}">
						<a class="btn primary" href="${cpath}/"
							style="margin-top: 18px; width: 100%; display: block; text-align: center;">
							투자 시작하기 </a>
					</c:when>
					<c:otherwise>
						<a class="btn primary" href="${cpath}/login"
							style="margin-top: 18px; width: 100%; display: block; text-align: center;">
							로그인 하러가기 </a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</body>
</html>
