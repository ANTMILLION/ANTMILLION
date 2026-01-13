<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - 서버 오류</title>
    <link rel="stylesheet" href="${cpath}/resources/css/error/error.css">
</head>
<body class="error-body">
    <div class="error-container">
        <div class="error-content">
            <div class="error-ant">
                <img src="${cpath}/resources/images/error/500.png" alt="심각한 오류">
            </div>
            <div class="error-code error-500">500</div>
            <h1 class="error-title">서버 오류가 발생했습니다</h1>
            <p class="error-message">
                일시적인 오류가 발생했습니다.<br>
                잠시 후 다시 시도해 주세요.
            </p>
            <div class="error-actions">
                <button class="btn-primary" onclick="location.href='${cpath}/main'">
                    홈으로 가기
                </button>
                <button class="btn-secondary" onclick="location.reload()">
                    새로고침
                </button>
            </div>
        </div>
    </div>
</body>
</html>
