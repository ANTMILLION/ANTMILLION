<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="sidebar">
  <div class="brand">
    <span class="brand-text">똑똑한개미</span>
  </div>

  <nav class="nav">
    <a class="nav-item" href="${pageContext.request.contextPath}/">메인</a>
    <a class="nav-item" href="#">종목</a>
    <a class="nav-item" href="#">마이페이지</a>
    <a class="nav-item" href="#">미션</a>
  </nav>

  <div class="spacer"></div>

  <div class="bottom">
    <a class="nav-item nav-item--bottom" href="${pageContext.request.contextPath}/login">
      <span class="nav-item-icon">&#x21A9;&#xFE0F;</span>
      <span>Log in</span>
    </a>
  </div>
</div>