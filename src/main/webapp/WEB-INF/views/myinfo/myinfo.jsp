<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 내 정보</title>
    <%@ include file="/WEB-INF/views/common/common.jsp" %>
    <link rel="stylesheet" href="${cpath}/resources/css/myinfo/myinfo.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
</head>
<body>
    <div class="myinfo-app-container">
        <%@ include file="../common/sidebar.jsp"%>
        <%@ include file="../common/header.jsp"%>
        <main class="myinfo-main-content">
            <!-- 내 정보 카드 -->
            <div class="myinfo-card">
                <div class="myinfo-header">
                    <h2 class="myinfo-title">내 정보</h2>
                    <div class="myinfo-date">2025. 1. 29 가입</div>
                </div>
                <div class="myinfo-content">
                    <!-- 프로필 섹션 -->
                    <div class="myinfo-profile-section">
                        <div class="myinfo-ant-image">
                            <img src="${cpath}/resources/images/profile/bronze-ant.png" alt="개미 캐릭터" />
                        </div>
                        <div class="myinfo-details">
                            <div class="myinfo-field">
                                <label class="myinfo-label">닉네임</label>
                                <div class="myinfo-value" id="myinfo-nickname">가은</div>
                            </div>
                            <div class="myinfo-field">
                                <label class="myinfo-label">이메일</label>
                                <div class="myinfo-value" id="myinfo-email">rkdms@naver.com</div>
                            </div>
                            <div class="myinfo-field">
                                <label class="myinfo-label">비밀번호</label>
                                <button class="myinfo-password-btn" id="changePasswordBtn">
                                    비밀번호 변경
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- 회원탈퇴 버튼 -->
                <div class="myinfo-footer">
                    <button class="myinfo-delete-btn" id="deleteAccountBtn">
                        회원탈퇴 하기
                    </button>
                </div>
            </div>
        </main>

        <!-- 비밀번호 변경 모달 -->
        <div class="myinfo-modal" id="passwordModal">
            <div class="myinfo-modal-content">
                <button class="myinfo-modal-close" onclick="closePasswordModal()">✕</button>
                <h2 class="myinfo-modal-title">비밀번호 변경</h2>
                <form class="myinfo-password-form" id="passwordForm">
                    <div class="myinfo-input-group">
                        <input type="password" class="myinfo-input" placeholder="기존 비밀번호 입력" id="currentPassword" required>
                    </div>
                    <div class="myinfo-input-group">
                        <input type="password" class="myinfo-input" placeholder="새 비밀번호 입력" id="newPassword" required>
                    </div>
                    <div class="myinfo-input-group">
                        <input type="password" class="myinfo-input" placeholder="새 비밀번호 입력" id="confirmPassword" required>
                    </div>
                    <button type="submit" class="myinfo-submit-btn">비밀번호 변경하기</button>
                </form>
            </div>
        </div>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
            const cpath = "${pageContext.request.contextPath}";
        </script>
        <script src="${cpath}/resources/js/myinfo/myinfo.js"></script>
    </div>
</body>
</html>