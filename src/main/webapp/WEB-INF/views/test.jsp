<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>똑똑한개미 - 히스토리</title>
<style>
    /* 기본 초기화 */
    body, html { margin: 0; padding: 0; font-family: 'Pretendard', sans-serif; background-color: #f4f7fa; height: 100%; }
    .container { display: flex; height: 100vh; }

    /* 사이드바 */
    .sidebar { width: 200px; background-color: #fff; border-right: 1px solid #e0e0e0; display: flex; flex-direction: column; padding: 20px; }
    .logo { font-weight: bold; font-size: 20px; display: flex; align-items: center; margin-bottom: 40px; }
    .nav-menu { flex: 1; }
    .nav-item { padding: 12px 10px; color: #666; cursor: pointer; display: flex; align-items: center; text-decoration: none; }
    .nav-item.active { color: #333; font-weight: bold; background-color: #f0f0f0; border-radius: 8px; }
    .nav-footer { border-top: 1px solid #eee; padding-top: 20px; }

    /* 메인 영역 */
    .main-content { flex: 1; padding: 20px 40px; overflow-y: auto; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .search-bar { background: #eef2f8; border: none; padding: 10px 20px; border-radius: 20px; width: 300px; }
    
    /* 카드 섹션 */
    .card-container { max-width: 800px; margin: 0 auto; }
    .filter-section { background: #fff; padding: 20px; border-radius: 12px; display: flex; gap: 15px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
    .select-box { border: 1px solid #4a90e2; padding: 8px 15px; border-radius: 4px; width: 200px; color: #4a90e2; font-weight: bold; }

    .content-card { background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); min-height: 500px; }
    .history-title { font-weight: bold; font-size: 18px; margin-bottom: 15px; }
    .history-text { line-height: 1.6; color: #333; margin-bottom: 25px; }
    
    /* 상세 정보 박스 */
    .detail-box { border: 1px solid #f0f0f0; padding: 20px; border-radius: 8px; }
    .date { color: #999; margin-bottom: 10px; }
    .stock-info { display: flex; justify-content: space-between; align-items: center; }
    .stock-name { font-weight: bold; font-size: 16px; }
    .price { font-weight: bold; }
    .percentage { color: #4a90e2; font-size: 14px; margin-left: 10px; }
    .buy-badge { color: red; font-weight: bold; margin-right: 5px; }
</style>
</head>
<body>

<div class="container">
    <div class="sidebar">
        <div class="logo">🐜 똑똑한개미</div>
        <div class="nav-menu">
            <div class="nav-item">🏠 메인</div>
            <div class="nav-item active">📈 종목</div>
            <div class="nav-item">👤 마이페이지</div>
            <div class="nav-item">🎯 미션</div>
        </div>
        <div class="nav-footer">
            <div class="nav-item">🕒 History</div>
            <div class="nav-item">Logout</div>
        </div>
    </div>

    <div class="main-content">
        <div class="header">
            <input type="text" class="search-bar" placeholder="종목을 검색하세요">
            <div class="user-info">🔔 John Doe ▼</div>
        </div>

        <div class="card-container">
            <div class="filter-section">
                <div>
                    <div style="font-size:12px; color:#666; margin-bottom:5px;">카테고리 별 조회</div>
                    <select class="select-box">
                        <option>전체</option>
                        <option selected>매몰비용 오류</option>
                        <option>손실회피</option>
                    </select>
                </div>
                <div style="flex:1">
                    <div style="font-size:12px; color:#666; margin-bottom:5px;">&nbsp;</div>
                    <input type="text" class="search-bar" style="width:100%" placeholder="종목을 검색하세요">
                </div>
            </div>

            <div class="content-card">
                <div class="history-title">매몰비용 오류 내역</div>
                <p class="history-text">
                    신한지주 종목의 추세가 하향(이동평균선 역배열 등)인데, 계속해서 추가 매수를 진행하여 비중이 지나치게 커지고 있음<br><br>
                    단순히 매입 단가를 낮추기 위한 매몰 비용 오류에 빠진 것은 아닌가요?
                </p>

                <div class="detail-box">
                    <div class="date">2026.01.02</div>
                    <div class="stock-info">
                        <div>
                            <div class="stock-name">신한지주</div>
                            <div style="font-size:13px; color:#666;"><span class="buy-badge">매수</span> 10주</div>
                        </div>
                        <div class="price">700,000원 <span class="percentage">(-10%)</span></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<style>
    /* 알림창 컨테이너 (기본은 숨김 상태) */
    .notification-dropdown {
        position: absolute;
        top: 60px;
        right: 40px;
        width: 360px;
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(10px); /* 유리 느낌 효과 */
        border-radius: 16px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
        border: 1px solid rgba(255, 255, 255, 0.3);
        z-index: 1000;
        overflow: hidden;
        display: none; /* 클릭 시 block으로 변경 */
        animation: slideDown 0.3s ease-out;
    }

    @keyframes slideDown {
        from { opacity: 0; transform: translateY(-10px); }
        to { opacity: 1; transform: translateY(0); }
    }

    .notif-header {
        padding: 15px 20px;
        background: #4a90e2;
        color: white;
        font-weight: bold;
        display: flex;
        justify-content: space-between;
    }

    /* 알림 리스트 아이템 */
    .notif-list { max-height: 400px; overflow-y: auto; }
    
    .notif-item {
        display: flex;
        align-items: center;
        padding: 15px 20px;
        border-bottom: 1px solid #f0f0f0;
        transition: background 0.2s;
        cursor: pointer;
    }

    .notif-item:hover { background: #f8fbff; }
    .notif-item:last-child { border-bottom: none; }

    /* 종목 로고 영역 */
    .stock-logo {
        width: 45px;
        height: 45px;
        background: #1a4da1; /* 삼성 블루 */
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
        color: white;
        font-size: 10px;
        font-weight: bold;
        flex-shrink: 0;
    }

    /* 텍스트 영역 */
    .notif-content { flex: 1; }
    .notif-title { font-size: 14px; font-weight: 700; color: #333; margin-bottom: 3px; }
    .notif-msg { font-size: 13px; color: #555; line-height: 1.4; }
    .notif-time { font-size: 11px; color: #999; margin-top: 5px; }
    
    /* 경고 아이콘 스타일 */
    .warning-text { color: #e74c3c; font-weight: 600; display: flex; align-items: center; gap: 4px; }
</style>

<div id="notifDropdown" class="notification-dropdown">
    <div class="notif-header">
        <span>알림 메세지</span>
        <span style="cursor:pointer" onclick="toggleNotif()">×</span>
    </div>
    <div class="notif-list">
        <div class="notif-item">
            <div class="stock-logo">SAMSUNG</div>
            <div class="notif-content">
                <div class="notif-title">(005930) 삼성전자</div>
                <div class="notif-msg">새로운 리포트가 등록되었습니다.</div>
                <div class="notif-time">2026/01/12 09:00:15</div>
            </div>
        </div>

        <div class="notif-item" style="background: #fff5f5;">
            <div class="stock-logo" style="background: #1a4da1;">SAMSUNG</div>
            <div class="notif-content">
                <div class="notif-title">(005930) 삼성전자</div>
                <div class="notif-msg warning-text">⚠️ 매몰 비용 오류 경고</div>
                <div class="notif-msg" style="font-size:12px;">손실 상태에서 동일 종목 추가 매수 감지</div>
                <div class="notif-time">2026/01/12 10:30:22</div>
            </div>
        </div>

        <div class="notif-item">
            <div class="stock-logo">SAMSUNG</div>
            <div class="notif-content">
                <div class="notif-title">(005930) 삼성전자</div>
                <div class="notif-msg">지정가 알림: 75,000원 도달</div>
                <div class="notif-time">2026/01/12 11:15:00</div>
            </div>
        </div>
    </div>
</div>

<script>
    // 알림창 켜고 끄는 함수
    function toggleNotif() {
        const dropdown = document.getElementById('notifDropdown');
        dropdown.style.display = (dropdown.style.display === 'block') ? 'none' : 'block';
    }
</script>
</body>
</html>