<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 헤더 -->
<header class="header">
    <div class="search-box">
        <input type="text" placeholder="중목을 검색하세요">
        <button class="search-btn">
            <img src="<%=request.getContextPath()%>/resources/images/icon/search.png" alt="검색" class="search-icon">
        </button>
    </div>
    
    <div class="user-info">
        <button class="notification-btn">
            <img src="<%=request.getContextPath()%>/resources/images/icon/alarm.png" alt="알림" class="alarm-icon">
        </button>
        <div class="user-profile">
            <span class="user-name">John Doe</span>
            <span class="dropdown-arrow">▼</span>
        </div>
    </div>
</header>