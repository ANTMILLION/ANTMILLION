// 페이지 로드 시 미션 상태 확인
$(document).ready(function() {
    checkMissionStatus();
    loadRankInfo();

    // 랭크 시스템 모달 열기
    $('#rank-system-btn').on('click', function() {
        $('#rank-modal').addClass('active');
    });

    // 랭크 시스템 모달 닫기
    $('#rank-modal-close').on('click', function() {
        $('#rank-modal').removeClass('active');
    });

    // 모달 외부 클릭 시 닫기
    $('#rank-modal').on('click', function(e) {
        if ($(e.target).is('#rank-modal')) {
            $('#rank-modal').removeClass('active');
        }
    });
});

// 오늘의 미션 달성률(퀴즈/뉴스) 조회
function checkMissionStatus() {
    $.ajax({
        url: cpath + '/mission/today-status',
        method: 'GET',
        success: function(data) {
            const solvedCount = data.solvedCount || 0;
            const progressPercent = (solvedCount / 2) * 100;

            $('#mission-progressBar').css('width', progressPercent + '%');
            $('#mission-progressStatus').text(Math.round(progressPercent) + '% 달성!');

            // 퀴즈 완료 상태 표시
            if (solvedCount > 1) {
                $('#mission-card-quiz').addClass('completed');
                $('#quiz-start-btn').text('완료됨');
                $('#quiz-start-btn').removeAttr('onclick');
            }
        },
        error: function(err) {
            console.error('미션 상태 조회 실패:', err);
        }
    });
}

// 랭크 정보 로드
function loadRankInfo() {
    $.ajax({
        url: cpath + '/mission/status',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            // 1. 모든 랭크 이미지와 라벨의 'active' 클래스 제거 (초기화)
            $('.mission-ant-ranks img').removeClass('active');
            $('.mission-rank-label').removeClass('active');

            // 2. 현재 랭크 활성화 (ID 셀렉터 주의: mission-rank-img-...)
            let targetImgId = 'mission-rank-img-' + data.rankName;
            let $targetImg = $(document.getElementById(targetImgId));

            if ($targetImg.length > 0) {
                $targetImg.addClass('active');
                // data-rank 속성으로 매칭되는 라벨 찾기
                $('.mission-rank-label[data-rank="' + data.rankName + '"]').addClass('active');
            }

            // 3. 프로그레스 바 및 포인트 정보 업데이트
            updateRankProgress(data);

            // 4. 헤더 UI 업데이트 (상단바 닉네임/랭크 등)
            updateHeaderUI(data);
        },
        error: function(xhr, status, error) {
            console.error('랭크 정보 조회 실패:', error);
            $('#mission-reward-message').text('랭크 정보를 불러오는 데 실패했습니다.');
        }
    });
}

// 랭크 진행률 바 및 텍스트 업데이트
function updateRankProgress(data) {
    // 포인트 라벨 업데이트
    $('#mission-reward-currentRankStartPoint').text(data.currentRankStartPoint + ' P');
    $('#mission-reward-nextRankPoint').text(data.nextRankPoint + ' P');
    $('#mission-reward-currentPoint').text(data.currentPoint + ' P');

    // 진행률 계산
    let percent = 0;

    if (data.nextRankPoint > 0) {
        // 현재 랭크 구간 내에서의 진행도 계산
        const rangeTotal = data.nextRankPoint - data.currentRankStartPoint;
        const rangeCurrent = data.currentPoint - data.currentRankStartPoint;

        if (rangeTotal > 0) {
            percent = (rangeCurrent / rangeTotal) * 100;
        }
    } else {
        // 최고 레벨(챌린저 등 nextRankPoint가 0이거나 없을 때)
        percent = 100;
    }

    // 퍼센트 범위 제한 (0~100)
    percent = Math.max(0, Math.min(100, percent));

    // 남은 포인트 메시지 설정
    if (data.rankName === "챌린저") {
        $('#mission-reward-message').html("축하합니다! <strong>최고 레벨</strong>에 도달했습니다!");
        $('#mission-reward-nextRankPoint').text("MAX");
    } else {
        // neededPoint가 서버에서 오지 않는다면 계산: data.nextRankPoint - data.currentPoint
        let needed = data.neededPoint ? data.neededPoint : (data.nextRankPoint - data.currentPoint);
        $('#mission-reward-message').html(`다음 랭크까지 <strong id="mission-points-needed">${needed.toLocaleString()} P</strong> 남았습니다.`);
    }

    // 애니메이션 효과
    setTimeout(() => {
        $('#mission-reward-progressBar').css('width', percent + '%');
        $('#mission-reward-currentPoint').css('left', percent + '%');
    }, 100);
}

// 헤더(상단바) UI 업데이트
function updateHeaderUI(data) {
    // 헤더 상단 닉네임
    const headerName = document.querySelector('.header-user-name');
    if (headerName) headerName.textContent = data.nickName;

    // 헤더 드롭다운 닉네임
    const profileName = document.querySelector('.header-profile-nickname');
    if (profileName) profileName.textContent = data.nickName;

    // 헤더 드롭다운 티어
    const rankName = document.querySelector('.header-profile-tier');
    if (rankName) rankName.textContent = data.rankName + " 개미";

    // 헤더 드롭다운 아바타 이미지
    const rankImg = document.querySelector('.header-profile-avatar');
    if (rankImg) {
        rankImg.src = cpath + '/' + data.rankImage;
        rankImg.alt = data.rankName;
    }
}