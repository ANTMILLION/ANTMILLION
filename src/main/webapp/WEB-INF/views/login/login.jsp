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
<link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css" />
<link rel="stylesheet" href="${cpath}/resources/css/login/sign.css" />
</head>
<body>
	<%@ include file="../common/sidebar.jsp"%>
	<div class="shell">
		<div class="content">
			<div class="card">
				<h1 class="title-top">ANTMILLION</h1>
				<div class="hr"></div>
				<h2 class="h2">로그인</h2>

				<form class="form" method="post" action="${cpath}/login">
					<input type="email" name="email" placeholder="이메일을 입력하세요" value="${email}" required />
					<input type="password" name="password" placeholder="비밀번호를 입력하세요" title="비밀번호는 8자리 이상, 영문/숫자를 포함하고 공백 없이 특수문자를 사용할 수 있습니다." pattern="^(?=.*[A-Za-z])(?=.*\d)\S{8,}$" required/>
					<c:if test="${not empty loginError}">
                        <div class="msg-danger">${loginError}</div>
                    </c:if>
					<button class="btn primary" type="submit">로그인</button>
				</form>

				<form style="margin-top: 10px;" method="get"
					action="${cpath}/kakao/login">
					<button class="btn kakao" type="submit" style="width: 100%;">카카오로
						로그인</button>
				</form>

				<div class="center-links">
					<a href="${cpath}/signup">회원가입</a>
				</div>
			</div>
		</div>
	</div>

</body>
</html>