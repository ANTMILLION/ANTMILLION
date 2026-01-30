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
            correctAnswers = statusData.quizCount || 0;
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
                    const quizTitle = document.querySelector('.quiz-quiz-date');
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
    document.getElementById('quiz-quizQuestion').textContent = quiz.question;

    const optionsContainer = document.getElementById('quiz-quizOptions');
    optionsContainer.innerHTML = '';

    quiz.options.forEach(option => {
        const button = document.createElement('button');
        button.className = 'quiz-option-btn';
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
    document.getElementById('quiz-progressBar').style.width = progress + '%';
    document.getElementById('quiz-progressStatus').textContent = progress + '% 달성!';
}

// 정답 모달 표시
function showCorrectModal(point, explanation) {
    const modal = document.getElementById('quiz-result-Modal');
    const pointsMessage = document.getElementById('quiz-pointsMessage');
    const modalTitle = modal.querySelector('.quiz-modal-title');
    const modalMessage = modal.querySelector('.quiz-modal-message');

    // 포인트 메시지 표시
    pointsMessage.textContent = point + ' 포인트를 획득하였습니다.';
    pointsMessage.style.display = 'block';

    modalTitle.textContent = '짝짝짝👏👏👏 정답입니다~';
    modalMessage.textContent = explanation;

    // 정답 스타일 추가
    modal.classList.add('correct');
    modal.classList.add('active');
}

// 오답 모달 표시
function showWrongModal(message) {
    const modal = document.getElementById('quiz-result-Modal');
    const pointsMessage = document.getElementById('quiz-pointsMessage');
    const modalTitle = modal.querySelector('.quiz-modal-title');
    const modalMessage = modal.querySelector('.quiz-modal-message');

    pointsMessage.style.display = 'none';
    modalTitle.textContent = message; // "다시 한번 생각해 보세요."
    modalMessage.textContent = '퀴즈를 맞히고 나의 개미 랭크를 높여보세요!';

    // 정답 스타일 제거
    modal.classList.remove('correct');
    modal.classList.add('active');
}

// 모달 닫기
function closeModal() {
    const modal = document.getElementById('quiz-result-Modal');
    // 정답 모달인 경우 다음 문제로
    if (modal.classList.contains('correct')) {
        closeModalAndNext();
    } else {
        // 오답 모달은 그냥 닫기만
        modal.classList.remove('active');
    }
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

// 완료 화면 표시
function showCompletion() {
    // 1. 퀴즈 질문과 보기 숨기기
    document.getElementById('quiz-quizContent').style.display = 'none';

    // 2. (선택사항) 날짜 헤더도 숨기고 싶다면 주석 해제
    document.querySelector('.quiz-quiz-header').style.display = 'none';

    // 3. 완료 화면 보이기
    const completionScreen = document.getElementById('quiz-completionScreen');
    completionScreen.style.display = 'block'; // 보이게 설정
    completionScreen.classList.add('active'); // CSS 효과를 위한 클래스 추가

    // 4. 진행률 100%로 강제 설정
    document.getElementById('quiz-progressBar').style.width = '100%';
    document.getElementById('quiz-progressStatus').textContent = '100% 달성!';
}

// 모달 외부 클릭 시 닫기
window.onclick = function (event) {
    const modal = document.getElementById('quiz-result-Modal');
    if (event.target === modal) {
        closeModal();
    }
}

// 모달 닫고 다음 문제로 이동
function closeModalAndNext() {
    const modal = document.getElementById('quiz-result-Modal');
    modal.classList.remove('active');
    modal.classList.remove('correct');
    goToNextQuiz();
}