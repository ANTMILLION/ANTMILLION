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
               <button id="emailSendBtn" class="btn small primary" type="button">인증번호 전송</button>
           </div>
            <div id="emailCheckMsg"></div>
        <div class="row">
            <input id="emailCodeInput" type="text" placeholder="인증번호 6자리"
                 maxlength="6" inputmode="numeric" style="flex:1; min-width:0;" />
               <button id="emailConfirmBtn" class="btn small primary" type="button">인증확인</button>
        </div>
        <div id="emailVerifyMsg"></div>

        <input type="hidden" id="emailVerified" value="N" />
        <input type="password" name="password" placeholder="비밀번호" title="비밀번호는 영문과 숫자를 포함해 8자리 이상이어야 합니다." pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$" required/>
        <input type="password" name="passwordConfirm" placeholder="비밀번호 확인" title="비밀번호는 영문과 숫자를 포함해 8자리 이상이어야 합니다." pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$" required/>

        <!-- 에러 메시지(있을 때만 표시) -->
        <c:if test="${not empty error}">
          <div class="msg-danger">${error}</div>
        </c:if>

        <button id="nextBtn" class="btn primary" type="submit" disabled>다음</button>
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
  
  const sendBtn = document.getElementById('emailSendBtn');
  const confirmBtn = document.getElementById('emailConfirmBtn');
  const codeInput = document.getElementById('emailCodeInput');
  const verifyMsg = document.getElementById('emailVerifyMsg');
  const verifiedFlag = document.getElementById('emailVerified');
  const nextBtn = document.getElementById('nextBtn');

  const emailRe = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

  function resetVerifyUI() {
    verifiedFlag.value = 'N';
    nextBtn.disabled = true;
    verifyMsg.className = '';
    verifyMsg.textContent = '';
  }
  
  emailInput.addEventListener('input', resetVerifyUI);

  
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
  
  sendBtn.addEventListener('click', async () => {
	    const email = (emailInput.value || '').trim();

	    if (!emailRe.test(email)) {
	      verifyMsg.className = 'msg-danger';
	      verifyMsg.textContent = '이메일 형식이 올바르지 않습니다.';
	      return;
	    }

	    // (선택) 전송 전에 중복 체크를 한번 더 해서 불필요 발송 방지
	    const checkRes = await fetch(`${cpath}/signup/check-email?email=` + encodeURIComponent(email));
	    const check = await checkRes.json();
	    if (!check.available) {
	      verifyMsg.className = 'msg-danger';
	      verifyMsg.textContent = check.message; // 이미 사용 중인 이메일 등
	      return;
	    }

	    resetVerifyUI();

	    const res = await fetch(`${cpath}/signup/email/send`, {
	      method: 'POST',
	      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
	      body: new URLSearchParams({ email })
	    });

	    const data = await res.json();
	    verifyMsg.className = data.ok ? 'msg-ok' : 'msg-danger';
	    verifyMsg.textContent = data.message || (data.ok ? '인증번호를 전송했습니다.' : '전송 실패');
	  });
  
  confirmBtn.addEventListener('click', async () => {
	    const email = (emailInput.value || '').trim();
	    const code = (codeInput.value || '').trim();

	    if (!emailRe.test(email)) {
	      verifyMsg.className = 'msg-danger';
	      verifyMsg.textContent = '이메일 형식이 올바르지 않습니다.';
	      return;
	    }
	    if (!/^\d{6}$/.test(code)) {
	      verifyMsg.className = 'msg-danger';
	      verifyMsg.textContent = '인증번호 6자리를 입력하세요.';
	      return;
	    }

	    const res = await fetch(`${cpath}/signup/email/confirm`, {
	      method: 'POST',
	      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
	      body: new URLSearchParams({ email, code })
	    });

	    const data = await res.json();
	    verifyMsg.className = data.ok ? 'msg-ok' : 'msg-danger';
	    verifyMsg.textContent = data.message || (data.ok ? '인증 완료' : '인증 실패');

	    if (data.ok) {
	      verifiedFlag.value = 'Y';
	      nextBtn.disabled = false; // 인증 성공하면 다음 활성화
	    }
	  });
  
  const formEl = document.querySelector('form'); // signup step1 form
  formEl.addEventListener('submit', (e) => {
    const verified = document.getElementById('emailVerified').value;

    if (verified !== 'Y') {
      e.preventDefault(); // submit 막기
      verifyMsg.className = 'msg-danger';
      verifyMsg.textContent = '이메일 인증을 완료해야 다음 단계로 진행할 수 있습니다.';
      // 인증번호 입력칸으로 포커스
      codeInput.focus();
    }
  });
</script>
</body>
</html>
