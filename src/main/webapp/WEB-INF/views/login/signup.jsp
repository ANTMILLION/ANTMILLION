<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            <input id="emailInput" type="text" name="email" placeholder="이메일을 입력하세요" value="${email}" />
               <button id="emailCheckBtn" class="btn small primary" type="button">중복 확인</button>
           </div>
            <div id="emailCheckMsg"></div>

        <input type="password" name="password" placeholder="비밀번호" />
        <input type="password" name="passwordConfirm" placeholder="비밀번호 확인" />

        <!-- 에러 메시지(있을 때만 표시) -->
        <c:if test="${not empty error}">
          <div class="msg-danger">${error}</div>
        </c:if>

        <button class="btn primary" type="submit">다음</button>
      </form>

      <form style="margin-top: 10px;" method="post" action="${cpath}/signup/kakao">
        <button class="btn kakao" type="submit" style="width: 100%;">카카오로 회원가입</button>
      </form>

      <div class="center-links">
        이미 계정이 있나요? <a href="${cpath}/login">로그인</a>
      </div>
    </div>
  </div>
</div>
<script>
  const cpath = '${cpath}';
  const emailBtn = document.getElementById('emailCheckBtn');
  const emailInput = document.getElementById('emailInput');
  const emailMsg = document.getElementById('emailCheckMsg');

  emailBtn.addEventListener('click', async () => {
    const email = (emailInput.value || '').trim();
    const res = await fetch(`${cpath}/signup/check-email?email=` + encodeURIComponent(email));
    const data = await res.json();

    emailMsg.className = data.available ? 'msg-ok' : 'msg-danger';
    emailMsg.textContent = data.message;
  });
</script>
</body>
</html>
