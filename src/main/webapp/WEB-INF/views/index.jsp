<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>ANTMILLION</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" />
</head>
<body>
    <div class="shell">
      <%@ include file="layout/sidebar.jsp" %>
      <div class="content">
        
<div class="card">
  <h1 class="title-top">ANTMILLION</h1>
  <div class="hr"></div>
  <div class="complete">
    <div class="label">메인 화면(더미)</div>
    <div class="note">UI 확인용. 로그인/회원가입에서 이동만 합니다.</div>
  </div>
  <div style="display:flex; justify-content:center; gap:10px; margin-top:22px;">
    <a class="btn outline" href="${pageContext.request.contextPath}/login" style="text-decoration:none;">로그인</a>
    <a class="btn primary" href="${pageContext.request.contextPath}/signup" style="text-decoration:none;">회원가입</a>
  </div>
</div>

      </div>
    </div>
</body>
</html>
