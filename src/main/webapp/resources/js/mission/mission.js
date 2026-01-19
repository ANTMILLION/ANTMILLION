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

    // 서버에 완료 전송
    submitCompletion();
}

// 정답 제출 (AJAX)
function submitAnswer(quizId, optionId, buttonElement) {
    const requestData = {
        userId : 1, // 세션 ID로 변경 필요
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