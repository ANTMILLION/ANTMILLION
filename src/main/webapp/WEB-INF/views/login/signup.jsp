<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="/WEB-INF/views/common/common.jsp"%>
<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>ANTMILLION</title>
<link rel="stylesheet" href="${cpath}/resources/css/login/sign.css" />
<link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css" />
</head>
<body>
	<div class="shell">
		<%@ include file="../common/sidebar.jsp"%>
		<div class="content">

			<div class="card">
				<h1 class="title-top">ANTMILLION</h1>
				<div class="hr"></div>
				<h2 class="h2">회원가입</h2>

				<form class="form" method="post" action="${cpath}/signup">
					<div class="row">
						<input type="text" name="email" placeholder="이메일을 입력하세요" />
						<button class="btn small primary" type="button">중복 확인</button>
					</div>
					<input type="password" name="password" placeholder="비밀번호" /> <input
						type="password" name="passwordConfirm" placeholder="비밀번호 확인" />
					<div class="msg-danger">이메일이 이미 존재합니다.</div>
					<button class="btn primary" type="submit">다음</button>
				</form>

				<form style="margin-top: 10px;" method="post"
					action="${cpath}/signup">
					<button class="btn kakao" type="submit" style="width: 100%;">카카오로
						회원가입</button>
				</form>

				<div class="center-links">
					이미 계정이 있나요? <a href="${cpath}/login">로그인</a>
				</div>
			</div>

		</div>
	</div>
</body>
</html>
