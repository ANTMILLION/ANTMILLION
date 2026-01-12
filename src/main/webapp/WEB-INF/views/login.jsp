<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>ANTMILLION</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/sign.css" />
</head>
<body>
		<div class="shell">
			<%@ include file="common/sidebar.jsp"%>
			<div class="content">

				<div class="card">
					<h1 class="title-top">ANTMILLION</h1>
					<div class="hr"></div>
					<h2 class="h2">로그인</h2>

					<form class="form" method="post"
						action="${pageContext.request.contextPath}/login">
						<input type="text" name="email" placeholder="이메일을 입력하세요" /> <input
							type="password" name="password" placeholder="비밀번호를 입력하세요" />
						<div class="msg-danger">이메일 또는 비밀번호가 올바르지 않습니다.</div>
						<button class="btn primary" type="submit">로그인</button>
					</form>

					<form style="margin-top: 10px;" method="post"
						action="${pageContext.request.contextPath}/login">
						<button class="btn kakao" type="submit" style="width: 100%;">카카오로
							로그인</button>
					</form>

					<div class="center-links">
						<a href="${pageContext.request.contextPath}/signup">회원가입</a>
					</div>
				</div>

			</div>
		</div>
</body>
</html>
