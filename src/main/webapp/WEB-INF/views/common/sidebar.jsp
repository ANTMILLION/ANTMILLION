<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 공통 사이드바 네비게이션 -->
<aside class="sidebar">
    <div class="logo">
        <img src="${pageContext.request.contextPath}/resources/image/sidebar/ant_icon.png" alt="로고" class="logo-image">
        <h1 class="logo-text">똑똑한개미</h1>
    </div>
    
    <nav class="nav-menu">
        <a href="index.jsp" class="nav-item">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <rect x="3" y="3" width="6" height="6" rx="1" fill="currentColor"/>
                <rect x="11" y="3" width="6" height="6" rx="1" fill="currentColor"/>
                <rect x="3" y="11" width="6" height="6" rx="1" fill="currentColor"/>
                <rect x="11" y="11" width="6" height="6" rx="1" fill="currentColor"/>
            </svg>
            <span>메인</span>
        </a>
        
        <a href="statistics.jsp" class="nav-item">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="2" fill="none"/>
                <path d="M10 10L10 4" stroke="currentColor" stroke-width="2"/>
                <path d="M10 10L14 14" stroke="currentColor" stroke-width="2"/>
            </svg>
            <span>종목</span>
        </a>
        
        <a href="marketplace.jsp" class="nav-item">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <rect x="2" y="4" width="16" height="12" rx="1" stroke="currentColor" stroke-width="2" fill="none"/>
                <path d="M2 8H18" stroke="currentColor" stroke-width="2"/>
                <circle cx="6" cy="11" r="1" fill="currentColor"/>
            </svg>
            <span>마이페이지</span>
        </a>
        
        <a href="mission.jsp" class="nav-item">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <rect x="4" y="3" width="12" height="14" rx="1" stroke="currentColor" stroke-width="2" fill="none"/>
                <path d="M7 7H13M7 10H13M7 13H10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span>미션</span>
        </a>
    </nav>
    
    <div class="sidebar-footer">
        <a href="history.jsp" class="footer-item">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="2" fill="none"/>
                <path d="M10 6V10L13 13" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span>History</span>
        </a>
        
        <a href="login.jsp" class="footer-item">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M13 16L18 10L13 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M18 10H6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <path d="M6 3H4C3.44772 3 3 3.44772 3 4V16C3 16.5523 3.44772 17 4 17H6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span>Log In</span>
        </a>
    </div>
</aside>
