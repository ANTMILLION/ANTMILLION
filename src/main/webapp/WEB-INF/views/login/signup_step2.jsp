<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/common.jsp" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>ANTMILLION</title>
  <link rel="stylesheet"
  href="${cpath}/resources/css/common/sidebar.css" />
  <link rel="stylesheet" href="${cpath}/resources/css/login/sign.css" />
</head>
<body>
    <div class="shell">
      <%@ include file="../common/sidebar.jsp" %>
      <div class="content">
        
<div class="card">
  <h1 class="title-top">ANTMILLION</h1>
  <div class="hr"></div>

  <form class="form" method="post" action="${cpath}/signup/step2">
    <div class="row">
      <input type="text" name="nickname" placeholder="닉네임을 입력하세요" />
      <button class="btn small primary" type="button">중복 확인</button>
    </div>
    <div class="helper">* 닉네임은 모의투자 기록 내역에 표시됩니다.</div>

    <div class="checkline">
      <input type="checkbox" /> <span>(필수) 서비스 이용약관 동의</span>
    </div>
    <textarea placeholder="서비스 이용약관 내용"></textarea>

    <div class="checkline">
      <input type="checkbox" /> <span>(필수) 개인정보 수집·이용 동의</span>
    </div>
    <textarea placeholder="개인정보 수집·이용 동의 내용"></textarea>

    <button class="btn primary" type="submit">회원가입 완료</button>
  </form>
</div>

      </div>
    </div>
</body>
</html>
