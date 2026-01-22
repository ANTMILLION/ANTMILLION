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
        url: cpath + '/mission/daily',
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            if (!data || data.length === 0) {
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

            currentQuizIndex = 0;
            loadQuiz(currentQuizIndex);
        },
        error: function (xhr, status, error) {
            console.error('퀴즈 데이터를 가져오는 중 오류 발생:', error);
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
function handleCorrectAnswer(buttonElement, optionId) {
    buttonElement.classList.add('correct');
    correctAnswers++;

    // 포인트 메시지 표시
    showPointsMessage();

    // 진행률 업데이트
    updateProgress();

    // 화면 클릭 시 즉시 다음 문제로
    const skipHandler = function () {
        clearTimeout(autoNextTimeout);
        document.removeEventListener('click', skipHandler);
        goToNextQuiz();
    };

    // 1초 후 자동으로 다음 문제로 이동
    let autoNextTimeout = setTimeout(() => {
        document.removeEventListener('click', skipHandler); // 2초가 지나면 클릭 리스너도 제거
        goToNextQuiz();
    }, 1000);

    setTimeout(() => {
        document.addEventListener('click', skipHandler);
    }, 100); // 현재 클릭 이벤트와 분리
}

// 오답 처리
function handleWrongAnswer(buttonElement) {
    buttonElement.classList.add('wrong');

    // 오답 모달 표시
    showWrongModal();

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

// 포인트 메시지 표시
function showPointsMessage() {
    const message = document.getElementById('mission-pointsMessage');
    message.style.display = 'block';

    setTimeout(() => {
        message.style.display = 'none';
    }, 1000);
}

// 진행률 업데이트
function updateProgress() {
    const progress = Math.round((correctAnswers / quizData.length) * 100);
    document.getElementById('mission-progressBar').style.width = progress + '%';
    document.getElementById('mission-progressStatus').textContent = progress + '% 달성!';
}

// 오답 모달 표시
function showWrongModal() {
    document.getElementById('mission-wrongModal').classList.add('active');
}

// 모달 닫기
function closeModal() {
    document.getElementById('mission-wrongModal').classList.remove('active');
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

    // 서버에 완료 전송
    submitCompletion();
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
        success: function (isCorrect) {
            if (isCorrect){
                handleCorrectAnswer(buttonElement, optionId);
            } else {
                handleWrongAnswer(buttonElement);
            }
        },
        error: function(xhr, status, error) {
            console.error('채점 요청 실패:', error);
            alert("채점 중 오류가 발생했습니다.");
            isAnswering = false; // 에러 시 다시 클릭 가능
        }
    })
}

// 퀴즈 완료 제출
function submitCompletion() {
    console.log('Quiz completed:', {
        totalQuestions: quizData.length,
        correctAnswers: correctAnswers,
        score: Math.round((correctAnswers / quizData.length) * 100)
    });
}

// 모달 외부 클릭 시 닫기
window.onclick = function (event) {
    const modal = document.getElementById('mission-wrongModal');
    if (event.target === modal) {
        closeModal();
    }
}