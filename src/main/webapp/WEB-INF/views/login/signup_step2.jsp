<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

      <form class="form" method="post" action="${cpath}/signup/step2">
        <div class="row">
            <input id="nicknameInput" type="text" name="nickname" placeholder="닉네임을 입력하세요" value="${nickname}" />
            <button id="nicknameCheckBtn" class="btn small primary" type="button">중복 확인</button>
        </div>
        <div id="nicknameCheckMsg"></div>

        <div class="helper">* 닉네임은 모의투자 기록 내역에 표시됩니다.</div>

        <div class="checkline">
            <input type="checkbox" name="agreeTerms" value="Y" required/>
            <span>(필수) 서비스 이용약관 동의</span>
            <button type="button" class="btn-link" data-open="#termsModal">보기</button>
        </div>

        <div class="checkline">
            <input type="checkbox" name="agreePrivacy" value="Y" required/>
            <span>(필수) 개인정보 수집·이용 동의</span>
            <button type="button" class="btn-link" data-open="#privacyModal">보기</button>
        </div>

        <c:if test="${not empty error}">
          <div class="msg-danger">${error}</div>
        </c:if>

        <button class="btn primary" type="submit">회원가입 완료</button>
      </form>
    </div>
  </div>
</div>
<!-- ✅ 서비스 이용약관 모달 -->
<div class="modal-backdrop" id="termsModal" aria-hidden="true">
  <div class="modal" role="dialog" aria-modal="true" aria-labelledby="termsTitle">
    <div class="modal-head">
      <h2 class="modal-title" id="termsTitle">서비스 이용약관</h2>
      <button type="button" class="modal-close" data-close>닫기</button>
    </div>
    <div class="modal-body">
      <pre><c:out value="${termsText}" /></pre>
    </div>
  </div>
</div>

<!-- ✅ 개인정보 수집·이용 동의 모달 -->
<div class="modal-backdrop" id="privacyModal" aria-hidden="true">
  <div class="modal" role="dialog" aria-modal="true" aria-labelledby="privacyTitle">
    <div class="modal-head">
      <h2 class="modal-title" id="privacyTitle">개인정보 수집·이용 동의</h2>
      <button type="button" class="modal-close" data-close>닫기</button>
    </div>
    <div class="modal-body">
      <pre><c:out value="${privacyText}" /></pre>
    </div>
  </div>
</div>
<script>
  const cpath = '${cpath}';
  const nickBtn = document.getElementById('nicknameCheckBtn');
  const nickInput = document.getElementById('nicknameInput');
  const nickMsg = document.getElementById('nicknameCheckMsg');

  nickBtn.addEventListener('click', async () => {
    const nickname = (nickInput.value || '').trim();
    const res = await fetch(`${cpath}/signup/check-nickname?nickname=` + encodeURIComponent(nickname));
    const data = await res.json();

    nickMsg.className = data.available ? 'msg-ok' : 'msg-danger';
    nickMsg.textContent = data.message;
  });
  
//모달 열기/닫기
  const openModal = (selector) => {
    const el = document.querySelector(selector);
    if (!el) return;
    el.classList.add('open');
    el.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
  };

  const closeModal = (modalEl) => {
    if (!modalEl) return;
    modalEl.classList.remove('open');
    modalEl.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = '';
  };

  document.addEventListener('click', (e) => {
    const openBtn = e.target.closest('[data-open]');
    if (openBtn) {
      openModal(openBtn.getAttribute('data-open'));
      return;
    }

    // 닫기 버튼
    if (e.target.matches('[data-close]')) {
      const modal = e.target.closest('.modal-backdrop');
      closeModal(modal);
      return;
    }

    // 바깥(백드롭) 클릭 시 닫기
    if (e.target.classList.contains('modal-backdrop')) {
      closeModal(e.target);
    }
  });

  // ESC 닫기
  document.addEventListener('keydown', (e) => {
    if (e.key !== 'Escape') return;
    const opened = document.querySelector('.modal-backdrop.open');
    if (opened) closeModal(opened);
  });
</script>
</body>
</html>
