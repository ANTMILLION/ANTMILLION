<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>똑똑한개미 - 마이페이지</title>
<%@ include file="/WEB-INF/views/common/common.jsp"%>
<link rel="stylesheet" href="${cpath}/resources/css/mypage/mypage.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
<link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
</head>
<body>
    <div class="mypage-app-container">
        <!-- 사이드바 include -->
        <%@ include file="../common/sidebar.jsp"%>

        <!-- 메인 콘텐츠 영역 -->
        <main class="mypage-main-content">
            <!-- 헤더 include -->
            <%@ include file="../common/header.jsp"%>

            <!-- 콘텐츠 영역 -->
            <div class="mypage-content-wrapper">
                <!-- 좌측 영역 -->
                <div class="mypage-left-section">
                    <!-- 탭 메뉴 -->
                    <div class="mypage-tab-menu">
                        <button class="mypage-tab-button active" data-tab="stock">주식잔고</button>
                        <button class="mypage-tab-button" data-tab="realized">실현손익</button>
                        <button class="mypage-tab-button" data-tab="executed">체결내역</button>
                        <button class="mypage-tab-button" data-tab="account">계좌정보</button>
                    </div>

                    <!-- 탭 콘텐츠 -->
                    <div class="mypage-tab-content">
                        <!-- 주식잔고 탭 -->
                        <div class="mypage-tab-panel active" id="stock-panel">
                            <h2 class="mypage-section-title">주식잔고</h2>

                            <!-- 주식 카드 목록 -->
                            <div class="mypage-stock-list" id="stockList">
                                <!-- JavaScript로 동적 생성 -->
                            </div>
                        </div>

                        <!-- 실현손익 탭 -->
                        <div class="mypage-tab-panel" id="realized-panel">
                            <h2 class="mypage-section-title">실현손익</h2>

                            <!-- 기간 선택 -->
                            <div class="mypage-date-filter">
                                <label class="mypage-date-label">기간</label>
                                <div class="mypage-date-inputs">
                                    <div class="mypage-date-input-wrapper">
                                        <input type="date" class="mypage-date-input"
                                            id="realizedStartDate" value="2026-01-15">
                                        <button class="mypage-date-clear"
                                            data-target="realizedStartDate">×</button>
                                    </div>
                                    <span class="mypage-date-separator">~</span>
                                    <div class="mypage-date-input-wrapper">
                                        <input type="date" class="mypage-date-input"
                                            id="realizedEndDate" value="2026-01-15">
                                        <button class="mypage-date-clear"
                                            data-target="realizedEndDate">×</button>
                                    </div>
                                </div>
                            </div>

                            <!-- 실현손익 목록 -->
                            <div class="mypage-realized-list" id="realizedList">
                                <!-- JavaScript로 동적 생성 -->
                            </div>
                        </div>

                        <!-- 체결내역 탭 -->
                        <div class="mypage-tab-panel" id="executed-panel">
                            <h2 class="mypage-section-title">체결내역</h2>

                            <!-- 기간 선택 -->
                            <div class="mypage-date-filter">
                                <label class="mypage-date-label">기간</label>
                                <div class="mypage-date-inputs">
                                    <div class="mypage-date-input-wrapper">
                                        <input type="date" class="mypage-date-input"
                                            id="executedStartDate" value="2026-01-15">
                                        <button class="mypage-date-clear"
                                            data-target="executedStartDate">×</button>
                                    </div>
                                    <span class="mypage-date-separator">~</span>
                                    <div class="mypage-date-input-wrapper">
                                        <input type="date" class="mypage-date-input"
                                            id="executedEndDate" value="2026-01-15">
                                        <button class="mypage-date-clear"
                                            data-target="executedEndDate">×</button>
                                    </div>
                                </div>
                            </div>

                            <!-- 서브 탭 -->
                            <div class="mypage-sub-tab-menu">
                                <button class="mypage-sub-tab-button active" data-subtab="all">전체</button>
                                <button class="mypage-sub-tab-button" data-subtab="executed">체결</button>
                                <button class="mypage-sub-tab-button" data-subtab="unexecuted">미체결</button>
                            </div>

                            <!-- 체결내역 테이블 -->
                            <div class="mypage-table-wrapper">
                                <table class="mypage-executed-table" id="executedTable">
                                    <thead>
                                        <tr>
                                            <th>종목명</th>
                                            <th>주문수량</th>
                                            <th>체결수량</th>
                                            <th>미체결수량</th>
                                            <th>미체결금액</th>
                                            <th>주문시간</th>
                                        </tr>
                                        <tr>
                                            <th>매매구분</th>
                                            <th>주문단가</th>
                                            <th>체결단가</th>
                                            <th>주문금액</th>
                                            <th>종목코드</th>
                                            <th>체결시간</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <!-- JavaScript로 동적 생성 -->
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- 계좌정보 탭 -->
                        <div class="mypage-tab-panel" id="account-panel">
						    <h2 class="mypage-section-title">계좌정보</h2>
						
						    <div class="account-card" style="margin-top: 20px;">
						        <h3 class="account-user-title" style="border-bottom: 1px solid #ddd; padding-bottom: 10px; margin-bottom: 15px;">
						            <!-- <span id="userNickname" class="negative" style="font-weight: bold;">로딩 중...</span>님의 계좌  -->
						            <span id="userNickname" style="color: #2271e9; font-weight: 800; margin-right: 2px;">(로딩 중)</span><span style="font-size: 0.99em; color: #333;">님의 계좌</span>
						        </h3>
						        
						        <div class="account-info-list" style="line-height: 2;">
						            <div class="account-detail-row">
						                <span class="label" style="width: 100px; display: inline-block; color: #666;">계좌번호</span>
						                <span class="value" id="accNumber" style="font-weight: 500;">-</span>
						            </div>
						            <div class="account-detail-row">
						                <span class="label" style="width: 100px; display: inline-block; color: #666;">잔고</span>
						                <span class="value accent" style="font-weight: bold;"><span id="accBalance">0</span>원</span>
						            </div>
						            <div class="account-detail-row">
						                <span class="label" style="width: 100px; display: inline-block; color: #666;">개설일</span>
						                <span class="value" id="accCreateDate" style="color: #666;">-</span>
						            </div>
						        </div>
						    </div>
						</div>
						
						
                    </div>
                </div>

                <!-- 우측 영역 -->
                <div class="mypage-right-section">
                    <!-- 총 자산 카드 -->
                    <div class="mypage-asset-card">
                        <h3 class="mypage-asset-title">내 자산 총액</h3>
                        <div class="mypage-asset-amount" id="totalAsset"></div>
                        <div class="mypage-asset-profit">
                            <span class="mypage-profit-label">총 평가 손익</span> <span
                                class="mypage-profit-amount positive" id="totalProfit"></span>
                        </div>
                    </div>

                    <!-- ✅✅✅ 심리경고 카드 - 수정된 부분 ✅✅✅ -->
                    <div class="mypage-alert-card">
                        <div class="mypage-alert-header">
                            <h3 class="mypage-alert-title">심리경고</h3>
                        </div>
                        
                        <!-- ✅ 기간 선택 - id 변경 -->
                        <div class="mypage-date-filter">
                            <label class="mypage-date-label">기간</label>
                            <div class="mypage-date-inputs">
                                <div class="mypage-date-input-wrapper">
                                    <!-- ✅ id를 alertStartDate로 변경, value 제거 -->
                                    <input type="date" class="mypage-date-input" id="alertStartDate">
                                    <button class="mypage-date-clear" data-target="alertStartDate">×</button>
                                </div>
                                <span class="mypage-date-separator">~</span>
                                <div class="mypage-date-input-wrapper">
                                    <!-- ✅ id를 alertEndDate로 변경, value 제거 -->
                                    <input type="date" class="mypage-date-input" id="alertEndDate">
                                    <button class="mypage-date-clear" data-target="alertEndDate">×</button>
                                </div>
                            </div>
                        </div>

                        <!-- ✅ 심리경고 목록 - JavaScript로 동적 생성됨 -->
                        <div class="mypage-alert-content">
                            <div class="mypage-alert-list">
                                <!-- JavaScript의 loadHistoryData()가 여기에 동적으로 추가함 -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <script>
        // 페이지 로드 시 스크롤을 맨 위로 이동
        window.onload = function() {
            window.scrollTo(0, 0);
        };

        // 페이지 진입 시 즉시 스크롤 초기화
        if (history.scrollRestoration) {
            history.scrollRestoration = 'manual';
        }
    </script>
    
    <script>
	    const contextPath = "${cpath}";
	</script>
    <!-- SockJS 라이브러리 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>

    <!-- STOMP 라이브러리 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <script src="${cpath}/resources/js/mypage/mypage.js"></script>
</body>
</html>
