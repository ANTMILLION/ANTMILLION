// 전역 변수로 읽은 목록 저장
let readUrls = [];

$(document).ready(function() {
    loadNews();
    loadMissionProgress();
});

function loadNews() {
    $.ajax({
        url: `${cpath}/api/news`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            readUrls = response.readList || [];
            displayNews(response.articles);
        },
        error: function(error) {
            console.error('뉴스 로딩 실패:', error);
            displayError();
        }
    });
}

function displayNews(articles) {
    const container = $('#newsContainer');
    container.empty();

    if (!articles || articles.length === 0) {
        container.html(`
            <div class="news-error">
                <h2 class="news-error-title">뉴스가 없습니다</h2>
                <p class="news-error-message">현재 표시할 뉴스가 없습니다.</p>
            </div>
        `);
        return;
    }

    articles.forEach((article, index) => {
        const newsCard = createNewsCard(article, index);
        container.append(newsCard);
    });
}

function createNewsCard(article, index) {
    // HTML 태그(<b>) 제거
    const cleanTitle = stripHtml(article.title);
    const cleanDescription = stripHtml(article.description);

    // 날짜 포맷팅
    const formattedDate = formatDate(article.pubDate);
    const safeLink = article.link.replace(/'/g, "\\'");

    const isRead = readUrls.includes(article.link);

    // 읽었으면 'read-badge' 클래스와 텍스트 표시
    const readBadgeHtml = isRead
        ? `<span class="news-read-badge">읽음</span>`
        : ``;

    return `
        <div class="news-card" onclick="handleNewsClick('${safeLink}', this)">
            <div class="news-card-header">
                <span class="news-source">네이버 뉴스</span>
                <span class="news-card-date">${formattedDate}</span>
                ${readBadgeHtml}
            </div>
            <h2 class="news-card-title">${cleanTitle}</h2>
            <p class="news-card-description">${cleanDescription}</p>
        </div>
    `;
}

// 뉴스 클릭 핸들러
function handleNewsClick(url, cardElement) {
    // 새 창으로 뉴스 띄우기
    window.open(url, '_blank');
    const $card = $(cardElement);

    // 서버에 읽음 기록 요청
    $.ajax({
        url: `${cpath}/api/news/read`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ newsUrl: url }),
        success: function(data) {
            // 성공일 경우 읽음 처리 (중복 추가 방지)
            if (!$card.hasClass('read')) {
                $card.addClass('read');
                // 헤더 영역을 찾아서 날짜 뒤에 배지 추가
                $card.find('.news-card-header').append('<span class="news-read-badge">읽음</span>');
            }
            // 달성률 업데이트
            updateProgressBar(data.progress);
        },
        error: function(xhr, status, error) {
            if (xhr.status === 401) {
                console.log("비로그인 상태입니다. 기록되지 않습니다.");
            } else {
                console.error("서버 오류:", error);
            }
        }
    });
}

// 미션 진행률 로딩
function loadMissionProgress() {
    $.ajax({
        url: `${cpath}/api/news/progress`,
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            updateProgressBar(data.progress);
        },
        error: function(xhr, status, error) {
            console.log("진행률 로딩 실패: ", error);
        }
    });
}

// 업데이트 함수
function updateProgressBar(percent) {
    const bar = document.getElementById('news-progressBar');
    const text = document.getElementById('news-progressStatus');

    if(bar && text) {
        bar.style.width = percent + '%';
        text.innerText = percent + '% 달성!';
    }
}

function stripHtml(html) {
    const tmp = document.createElement('DIV');
    tmp.innerHTML = html;
    return tmp.textContent || tmp.innerText || '';
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}`;
}

function displayError() {
    const container = $('#newsContainer');
    container.html(`
        <div class="news-error">
            <h2 class="news-error-title">뉴스를 불러올 수 없습니다</h2>
        </div>
    `);
}