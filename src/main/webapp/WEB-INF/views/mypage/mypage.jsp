<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>똑똑한개미 - 마이페이지</title>
    <%@ include file="/WEB-INF/views/common/common.jsp" %>
    <link rel="stylesheet" href="${cpath}/resources/css/common/sidebar.css">
    <link rel="stylesheet" href="${cpath}/resources/css/common/header.css">
    <link rel="stylesheet" href="${cpath}/resources/css/mypage/mypage.css">
</head>
<body>
<div class="mypage-app-container">
    <%@ include file="../common/sidebar.jsp" %>
    <%@ include file="../common/header.jsp" %>
    <main class="mypage-main-content">
        <div class="mypage-grid">
            <!-- 왼쪽 섹션 -->
            <div class="mypage-left-section">
                <!-- 자산 총액 카드 -->
                <div class="mypage-total-card">
                    <span class="mypage-total-label">내 자산 총액</span>
                    <div class="mypage-total-amount">2,833,000원</div>
                    <div class="mypage-profit-info">
                        <span class="mypage-profit-label">총 평가 손익</span>
                        <span class="mypage-profit-amount positive">276,000원 (+27.14%)</span>
                    </div>
                </div>

                <!-- 보유 주식 조회 카드 -->
                <div class="mypage-stocks-card">
                    <div class="mypage-stocks-header">
                        <h2 class="mypage-stocks-title">보유 주식 조회</h2>
                    </div>
                    <div class="mypage-stock-item">
                        <div class="mypage-stock-main">
                            <span class="mypage-stock-name">삼성전자</span>
                            <span class="mypage-stock-value">1,293,000원</span>
                        </div>
                        <div class="mypage-stock-details">
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">현금 10주</span>
                                <span class="mypage-stock-detail-value"></span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">평균단가</span>
                                <span class="mypage-stock-detail-value mypage-stock-profit">77,000원</span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">매수 금액</span>
                                <span class="mypage-stock-detail-value">1,017,000원</span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">원재가</span>
                                <span class="mypage-stock-detail-value">129,300원</span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">평가 금액</span>
                                <span class="mypage-stock-detail-value">1,293,000원</span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label"></span>
                                <span class="mypage-stock-detail-value mypage-stock-profit">276,000원 (+27.14%)</span>
                            </div>
                        </div>
                    </div>
                    <div class="mypage-stock-item">
                        <div class="mypage-stock-main">
                            <span class="mypage-stock-name">신한지주</span>
                            <span class="mypage-stock-value">1,540,000원</span>
                        </div>
                        <div class="mypage-stock-details">
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">현금 20주</span>
                                <span class="mypage-stock-detail-value"></span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">평균단가</span>
                                <span class="mypage-stock-detail-value mypage-stock-profit">77,000원</span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">매수 금액</span>
                                <span class="mypage-stock-detail-value">1,540,000원</span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">원재가</span>
                                <span class="mypage-stock-detail-value">77,000원</span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label">평가 금액</span>
                                <span class="mypage-stock-detail-value">1,540,000원</span>
                            </div>
                            <div class="mypage-stock-detail-item">
                                <span class="mypage-stock-detail-label"></span>
                                <span class="mypage-stock-detail-value">0 (0.00%)</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 오른쪽 섹션 - 거래 내역 -->
            <div class="mypage-right-section">
                <div class="mypage-history-card">
                    <div class="mypage-history-header">
                        <h2 class="mypage-history-title">거래 내역 조회</h2>
                        <div class="mypage-history-tabs">
                            <button class="mypage-tab-btn active">체결</button>
                            <button class="mypage-tab-btn">미체결</button>
                        </div>
                    </div>

                    <div class="mypage-history-list">
                        <div class="mypage-history-item">
                            <div class="mypage-history-date">2026.01.02</div>
                            <div class="mypage-history-info">
                                <div>
                                    <span class="mypage-history-type buy">신한지주</span>
                                    <span class="mypage-history-type buy">매수 10주</span>
                                </div>
                                <span class="mypage-history-amount buy">770,000원</span>
                            </div>
                            <div class="mypage-history-details">
                                <div class="mypage-history-detail-row">
                                    <span class="mypage-history-detail-label"></span>
                                    <span class="mypage-history-detail-value">77,000원</span>
                                </div>
                            </div>
                        </div>
                        <div class="mypage-history-item">
                            <div class="mypage-history-date">2025.12.31</div>
                            <div class="mypage-history-info">
                                <div>
                                    <span class="mypage-history-type buy">신한지주</span>
                                    <span class="mypage-history-type buy">매수 10주</span>
                                </div>
                                <span class="mypage-history-amount buy">770,000원</span>
                            </div>
                            <div class="mypage-history-details">
                                <div class="mypage-history-detail-row">
                                    <span class="mypage-history-detail-label"></span>
                                    <span class="mypage-history-detail-value">77,000원</span>
                                </div>
                            </div>
                        </div>
                        <div class="mypage-history-item">
                            <div class="mypage-history-date">2025.10.29</div>
                            <div class="mypage-history-info">
                                <div>
                                    <span class="mypage-history-type buy">삼성전자</span>
                                    <span class="mypage-history-type buy">매수 10주</span>
                                </div>
                                <span class="mypage-history-amount buy">1,017,000원</span>
                            </div>
                            <div class="mypage-history-details">
                                <div class="mypage-history-detail-row">
                                    <span class="mypage-history-detail-label"></span>
                                    <span class="mypage-history-detail-value">101,700원</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<script>
    // 체결/미체결 탭 전환 기능
    document.querySelectorAll('.mypage-tab-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.mypage-tab-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
        });
    });
</script>
</body>
</html>