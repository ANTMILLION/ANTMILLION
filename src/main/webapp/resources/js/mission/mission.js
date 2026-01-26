let quizData = [];
let currentQuizIndex = 0;
let correctAnswers = 0;
let isAnswering = false;

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function () {
    getDailyQuiz();
});

function formatTodayKorean() {
    const today = new Date();
    const month = today.getMonth() + 1;
    const date = today.getDate();
    const dayNames = ['일', '월', '화', '수', '목', '금', '토'];
    const day = dayNames[today.getDay()];
    return `${month}월 ${date}일 ${day}요일 오늘의 경제 퀴즈`;
}

// 서버 API 호출하여 데이터 가져오기
function getDailyQuiz() {
    $.ajax({
        url: cpath + "/mission/today-status",
        type: 'GET',
        dataType: 'json',
        success: function (statusData) {
            correctAnswers = statusData.solvedCount || 0;
            $.ajax({
                url: cpath + '/mission/daily',
                type: 'GET',
                dataType: 'json',
                success: function (data) {
                    if ((!data || data.length === 0) && correctAnswers >= 2) {
                        showCompletion();
                        return;
                    }

                    // 날짜 변경
                    const quizTitle = document.querySelector('.mission-quiz-date');
                    if (quizTitle) {
                        quizTitle.textContent = formatTodayKorean();
                    }

                    quizData = data.map(q => ({
                        id: q.quizId,
                        type: q.type,
                        question: q.question,
                        point: q.point,
                        options: q.choices.map((c, index) => ({
                            id: c.quizChoiceId,
                            no: c.choiceNo,
                            text: c.choiceText,
                        }))
                    }));

                    totalQuizCount = quizData.length + correctAnswers;
                    currentQuizIndex = 0;
                    updateProgress();
                    if (quizData.length > 0) {
                        loadQuiz(currentQuizIndex);
                    }
                },
                error: function (xhr, status, error) {
                    console.error('퀴즈 데이터를 가져오는 중 오류 발생:', error);
                }
            });
        }
    });
}

// 퀴즈 로드
function loadQuiz(index) {
    if (index >= quizData.length) {
        showCompletion();
        return;
    }

    const quiz = quizData[index];
    document.getElementById('mission-quizQuestion').textContent = quiz.question;

    const optionsContainer = document.getElementById('mission-quizOptions');
    optionsContainer.innerHTML = '';

    quiz.options.forEach(option => {
        const button = document.createElement('button');
        button.className = 'mission-option-btn';
        button.textContent = option.text;
        button.setAttribute('data-option-id', option.id);
        button.onclick = () => selectOption(option.no, null, button);
        optionsContainer.appendChild(button);
    });

    isAnswering = false;
}

// 선택지 선택
function selectOption(optionId, ignore, buttonElement) {
    if (isAnswering) return;
    isAnswering = true;

    // 현재 퀴즈ID 가져오기
    const currentQuizId = quizData[currentQuizIndex].id;

    // 서버로 정답 제출 및 채점 요청
    submitAnswer(currentQuizId, optionId, buttonElement);
}

// 정답 처리
function handleCorrectAnswer(buttonElement, optionId, response) {
    buttonElement.classList.add('correct');
    correctAnswers++;

    // 정답 모달 표시
    showCorrectModal(response.point, response.message);

    // 진행률 업데이트
    updateProgress();
}

// 오답 처리
function handleWrongAnswer(buttonElement, message) {
    buttonElement.classList.add('wrong');

    // 오답 모달 표시
    showWrongModal(message);

    // 1초 후 원래 상태로
    setTimeout(() => {
        buttonElement.classList.remove('wrong');
        isAnswering = false;
    }, 1000);
}

// 다음 퀴즈로 이동
function goToNextQuiz() {
    currentQuizIndex++;
    loadQuiz(currentQuizIndex);
}

// 진행률 업데이트
function updateProgress() {
    const progress = Math.round((correctAnswers / totalQuizCount) * 100);
    document.getElementById('mission-progressBar').style.width = progress + '%';
    document.getElementById('mission-progressStatus').textContent = progress + '% 달성!';
}

// 정답 모달 표시
function showCorrectModal(point, explanation) {
    const modal = document.getElementById('mission-result-Modal');
    const pointsMessage = document.getElementById('mission-pointsMessage');
    const modalTitle = modal.querySelector('.mission-modal-title');
    const modalMessage = modal.querySelector('.mission-modal-message');

    // 포인트 메시지 표시
    pointsMessage.textContent = point + ' 포인트 획득하였습니다.';
    pointsMessage.style.display = 'block';

    modalTitle.textContent = '짝짝짝👏👏👏 정답입니다~';
    modalMessage.textContent = explanation;

    // 정답 스타일 추가
    modal.classList.add('correct');
    modal.classList.add('active');
}

// 오답 모달 표시
function showWrongModal(message) {
    const modal = document.getElementById('mission-result-Modal');
    const pointsMessage = document.getElementById('mission-pointsMessage');
    const modalTitle = modal.querySelector('.mission-modal-title');
    const modalMessage = modal.querySelector('.mission-modal-message');

    pointsMessage.style.display = 'none';
    modalTitle.textContent = message; // "다시 한번 생각해 보세요."
    modalMessage.textContent = '퀴즈를 맞히고 나의 개미 랭크를 높여보세요!';

    // 정답 스타일 제거
    modal.classList.remove('correct');
    modal.classList.add('active');
}

// 모달 닫기
function closeModal() {
    const modal = document.getElementById('mission-result-Modal');
    // 정답 모달인 경우 다음 문제로
    if (modal.classList.contains('correct')) {
        closeModalAndNext();
    } else {
        // 오답 모달은 그냥 닫기만
        modal.classList.remove('active');
    }
}

// 완료 화면 표시
function showCompletion() {
    document.getElementById('mission-quizContent').style.display = 'none';
    document.getElementById('mission-completionScreen').classList.add('active');

    const quizTitle = document.querySelector('.mission-quiz-date');
    if (quizTitle) {
        quizTitle.textContent = '개미 랭크 시스템';
    }

    // 진행률 100%로
    document.getElementById('mission-progressBar').style.width = '100%';
    document.getElementById('mission-progressStatus').textContent = '100% 달성!';

    // 서버에서 랭크 정보 가져오기
    $.ajax({
        url: cpath + '/mission/status',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            // 모든 랭크 이미지와 라벨의 'active' 클래스 제거 (초기화)
            $('.mission-ant-ranks img').removeClass('active');
            $('.mission-rank-label').removeClass('active');

            // 사용자 랭크에 해당하는 이미지와 라벨에 'active' 추가
            let targetImgId = 'mission-rank-img-' + data.rankName;
            let $targetImg = $(document.getElementById(targetImgId));

            if ($targetImg.length > 0) {
                $targetImg.addClass('active');
                // data-rank 속성으로 매칭되는 라벨 찾기
                $('.mission-rank-label[data-rank="' + data.rankName + '"]').addClass('active');
            } else {
                console.log("일치하는 랭크 이미지를 찾을 수 없습니다: " + data.rankName);
            }

            // 포인트 정보 업데이트
            updateRankProgress(data);

            updateHeaderUI(data);
        },
        error: function(xhr, status, error) {
            console.error('랭크 정보 조회 실패:', error);
            $('#mission-reward-message').text('랭크 정보를 불러오는 데 실패했습니다.');
        }
    });
}

// 헤더(상단바)의 정보 업데이트
function updateHeaderUI(data) {
    // 헤더 상단 닉네임 변경
    const headerName = document.querySelector('.header-user-name');
    if (headerName) headerName.textContent = data.nickName;

    // 헤더 드롭다운 안의 닉네임 변경
    const profileName = document.querySelector('.header-profile-nickname');
    if (profileName) profileName.textContent = data.nickName;

    // 헤더 드롭다운 안의 랭크 이름(티어) 변경
    const rankName = document.querySelector('.header-profile-tier');
    if (rankName) rankName.textContent = data.rankName + " 개미";

    // 헤더 드롭다운 안의 이미지 변경
    const rankImg = document.querySelector('.header-profile-avatar');
    if (rankImg) {
        rankImg.src = cpath + '/' + data.rankImage;
        rankImg.alt = data.rankName;
    }
}

// 랭크 진행률 업데이트 함수 (새로 분리)
function updateRankProgress(data) {
    // 포인트 라벨 업데이트
    $('#mission-reward-currentRankStartPoint').text(data.currentRankStartPoint + ' P');
    $('#mission-reward-nextRankPoint').text(data.nextRankPoint + ' P');
    $('#mission-reward-currentPoint').text(data.currentPoint + ' P');

    // 진행률 계산
    let percent = 0;

    if (data.nextRankPoint > 0) {
        // 현재 랭크 구간 내에서의 진행도 계산
        const rangeTotal = data.nextRankPoint - data.currentRankStartPoint;  // 현재 랭크 구간 전체
        const rangeCurrent = data.currentPoint - data.currentRankStartPoint; // 현재 랭크 구간 내 진행도

        if (rangeTotal > 0) {
            percent = (rangeCurrent / rangeTotal) * 100;
        }
    } else {
        // 최고 레벨인 경우
        percent = 100;
    }

    // 퍼센트 범위 제한
    percent = Math.max(0, Math.min(100, percent));

    // 남은 포인트 메시지 설정
    if (data.rankName === "챌린저") {
        $('#mission-reward-message').text("축하합니다! 최고 레벨에 도달했습니다!");
    } else {
        $('#mission-reward-message').text(`다음 랭크까지 ${data.neededPoint.toLocaleString()} 포인트가 남았습니다.`);
    }

    // 애니메이션 효과를 위해 약간의 지연 후 포인트 바 업데이트
    setTimeout(() => {
        $('#mission-reward-progressBar').css('width', percent + '%');
        // 현재 포인트 라벨을 포인트 바 끝에 위치
        $('#mission-reward-currentPoint').css('left', percent + '%');
    }, 100);
}

// 정답 제출 (AJAX)
function submitAnswer(quizId, optionId, buttonElement) {
    const requestData = {
        userId : currentUserId,
        quizId : quizId,
        choiceNo : optionId
    }
    $.ajax({
        url: cpath + '/mission/check',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(requestData),
        dataType: 'json',
        success: function (response) {
            if (response.isCorrect){
                handleCorrectAnswer(buttonElement, optionId, response);
            } else {
                handleWrongAnswer(buttonElement, response.message);
            }
        },
        error: function(xhr, status, error) {
            console.error('채점 요청 실패:', error);
            alert("채점 중 오류가 발생했습니다.");
            isAnswering = false; // 에러 시 다시 클릭 가능
        }
    })
}

// 모달 외부 클릭 시 닫기
window.onclick = function (event) {
    const modal = document.getElementById('mission-result-Modal');
    if (event.target === modal) {
        closeModal();
    }
}

// 모달 닫고 다음 문제로 이동
function closeModalAndNext() {
    const modal = document.getElementById('mission-result-Modal');
    modal.classList.remove('active');
    modal.classList.remove('correct');
    goToNextQuiz();
}