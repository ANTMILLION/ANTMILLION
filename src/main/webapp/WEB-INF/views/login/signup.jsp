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
            <input id="emailInput" type="email" name="email" placeholder="이메일을 입력하세요" value="${form.email}" required />
               <button id="emailCheckBtn" class="btn small primary" type="button">중복 확인</button>
           </div>
            <div id="emailCheckMsg"></div>

        <input type="password" name="password" placeholder="비밀번호" title="비밀번호는 영문과 숫자를 포함해 8자리 이상이어야 합니다." pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$" required/>
        <input type="password" name="passwordConfirm" placeholder="비밀번호 확인" title="비밀번호는 영문과 숫자를 포함해 8자리 이상이어야 합니다." pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$" required/>

        <!-- 에러 메시지(있을 때만 표시) -->
        <c:if test="${not empty error}">
          <div class="msg-danger">${error}</div>
        </c:if>

        <button class="btn primary" type="submit">다음</button>
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
    const emailRe = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!emailRe.test(email)) {
    	  emailMsg.className = 'msg-danger';
    	  emailMsg.textContent = '이메일 형식이 올바르지 않습니다.';
    	  return;
    	}
    emailMsg.className = data.available ? 'msg-ok' : 'msg-danger';
    emailMsg.textContent = data.message;
  });
</script>
</body>
</html>
