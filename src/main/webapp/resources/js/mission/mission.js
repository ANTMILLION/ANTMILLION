// 퀴즈 데이터 (서버에서 AJAX로 가져올 예정)
let quizData = [];
let currentQuizIndex = 0;
let correctAnswers = 0;
let isAnswering = false;

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function () {
    loadQuizData();
});

// 퀴즈 데이터 로드 (AJAX)
function loadQuizData() {
    // 임시 데이터
    quizData = [
        {
            id: 1,
            question: "위험을 줄이기 위해 자산을 여러 곳에 나누어 투자하는 원칙을 분산투자라고 한다.",
            options: [
                {id: 1, text: "O", isCorrect: true},
                {id: 2, text: "X", isCorrect: false}
            ]
        },
        {
            id: 2,
            question: "주가가 하락할 때 주식을 빌려 파는 것은?",
            options: [
                {id: 3, text: "매수", isCorrect: false},
                {id: 4, text: "공매도", isCorrect: true},
                {id: 5, text: "손절매", isCorrect: false},
                {id: 6, text: "물타기", isCorrect: false}
            ]
        }
    ];
    loadQuiz(currentQuizIndex);
}

// 퀴즈 로드
function loadQuiz(index) {
    if (index >= quizData.length) {
        showCompletion();
        return;
    }

    const quiz = quizData[index];
    document.getElementById('quizQuestion').textContent = quiz.question;

    const optionsContainer = document.getElementById('quizOptions');
    optionsContainer.innerHTML = '';

    quiz.options.forEach(option => {
        const button = document.createElement('button');
        button.className = 'option-btn';
        button.textContent = option.text;
        button.setAttribute('data-option-id', option.id);
        button.setAttribute('data-is-correct', option.isCorrect);
        button.onclick = () => selectOption(option.id, option.isCorrect, button);
        optionsContainer.appendChild(button);
    });

    isAnswering = false;
}

// 선택지 선택
function selectOption(optionId, isCorrect, buttonElement) {
    if (isAnswering) return;
    isAnswering = true;

    if (isCorrect) {
        // 정답 처리
        handleCorrectAnswer(buttonElement, optionId);
    } else {
        // 오답 처리
        handleWrongAnswer(buttonElement);
    }
}

// 정답 처리
function handleCorrectAnswer(buttonElement, optionId) {
    buttonElement.classList.add('correct');
    correctAnswers++;

    // 포인트 메시지 표시
    showPointsMessage();

    // 진행률 업데이트
    updateProgress();

    // 서버에 정답 전송 (AJAX)
    submitAnswer(quizData[currentQuizIndex].id, optionId, true);

    // 2초 후 자동으로 다음 문제로 이동 (클릭하면 즉시)
    let autoNextTimeout = setTimeout(goToNextQuiz, 2000);

    // 화면 클릭 시 즉시 다음 문제로
    const skipHandler = function () {
        clearTimeout(autoNextTimeout);
        goToNextQuiz();
        document.removeEventListener('click', skipHandler);
    };

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
    const message = document.getElementById('pointsMessage');
    message.style.display = 'block';

    setTimeout(() => {
        message.style.display = 'none';
    }, 2000);
}

// 진행률 업데이트
function updateProgress() {
    const progress = Math.round((correctAnswers / quizData.length) * 100);
    document.getElementById('progressBar').style.width = progress + '%';
    document.getElementById('progressStatus').textContent = progress + '% 달성!';
}

// 오답 모달 표시
function showWrongModal() {
    document.getElementById('wrongModal').classList.add('active');
}

// 모달 닫기
function closeModal() {
    document.getElementById('wrongModal').classList.remove('active');
}

// 완료 화면 표시
function showCompletion() {
    document.getElementById('quizContent').style.display = 'none';
    document.getElementById('completionScreen').classList.add('active');

    const quizTitle = document.querySelector('.quiz-date');
    if (quizTitle) {
        quizTitle.textContent = '개미 랭크 시스템';
    }

    // 진행률 100%로
    document.getElementById('progressBar').style.width = '100%';
    document.getElementById('progressStatus').textContent = '100% 달성!';

    // 서버에 완료 전송
    submitCompletion();
}

// 퀴즈 닫기
function closeQuiz() {
    if (confirm('퀴즈를 종료하시겠습니까?')) {
        const contextPath = document.querySelector('script[src*="mission.js"]')
            .src.split('/js/')[0].replace(window.location.origin, '');
        location.href = contextPath + '/main.jsp';
    }
}

// 정답 제출 (AJAX)
function submitAnswer(quizId, optionId, isCorrect) {
    console.log('Submit answer:', {quizId, optionId, isCorrect});
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
    const modal = document.getElementById('wrongModal');
    if (event.target === modal) {
        closeModal();
    }
}