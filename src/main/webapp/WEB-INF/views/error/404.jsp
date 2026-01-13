<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - 페이지를 찾을 수 없습니다</title>
    <link rel="stylesheet" href="${cpath}/resources/css/error/error.css">
</head>
<body class="error-body">
    <div class="error-container">
        <div class="error-content">
            <div class="error-ant">
                <img src="${cpath}/resources/images/error/404.png" alt="길 잃은 개미">
            </div>
            <div class="error-code">404</div>
            <h1 class="error-title">페이지를 찾을 수 없습니다</h1>
            <p class="error-message">
                요청하신 페이지가 존재하지 않거나<br>
                삭제되었거나 주소가 변경되었습니다.
            </p>
            <div class="error-actions">
                <button class="btn-primary" onclick="location.href='${cpath}/main'">
                    홈으로 가기
                </button>
                <button class="btn-secondary" onclick="history.back()">
                    이전 페이지
                </button>
            </div>
        </div>
    </div>
</body>
</html>
