<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>ANTMILLION</title>
  <link rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/css/common/sidebar.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login/sign.css" />
</head>
<body>
    <div class="shell">
      <%@ include file="../common/sidebar.jsp" %>
      <div class="content">
        
<div class="card">
  <h1 class="title-top">ANTMILLION</h1>
  <div class="hr"></div>

  <div class="complete">
    <div class="label">계좌번호</div>
    <div class="value">ANT-000000</div>
  </div>

  <div class="complete" style="padding-top:10px;">
    <div class="label">지급 금액</div>
    <div class="value">₩ 30,000,000</div>
  </div>

  <div class="note">* 본 계좌는 모의투자용의 가상 계좌입니다.</div>

  <form method="post" action="${pageContext.request.contextPath}/signup/complete" style="margin-top:18px;">
    <button class="btn primary" type="submit" style="width:100%;">투자 시작하기</button>
  </form>
</div>

      </div>
    </div>
</body>
</html>
