let readUrls = []; // 전역 변수로 읽은 목록 저장
let currentPage = 1; // 현재 조회 중인 뉴스 페이지 번호
const pageSize = 5; // 한 페이지에 표시할 뉴스 개수
let maxPage = 1; // 전체 뉴스 기준 최대 페이지 수

$(document).ready(function() {
    loadNews(1);
    loadMissionProgress();
});

function loadNews(page) {
    $.ajax({
        url: `${cpath}/api/naver/news`,
        method: 'GET',
        data: {
            page: page,
            size: pageSize
        },
        dataType: 'json',
        success: function(response) {
            readUrls = response.readList || [];
            currentPage = response.currentPage;
            maxPage = response.maxPage;
            displayNews(response.articles);
            renderPagination();
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
    const readIndex = readUrls.indexOf(article.link);
    const isRead = readUrls.includes(article.link);

    const isPointEarned = isRead && readIndex < 5;
    let badgesHtml = '';

    if (isRead) {
        badgesHtml += `<span class="news-read-badge">읽음</span>`;
        // 포인트 획득 대상이면 뱃지 영구 표시
        if (isPointEarned) {
            badgesHtml += `<span class="news-points-badge">+100P</span>`;
        }
    }
    return `
        <div class="news-card" onclick="handleNewsClick('${safeLink}', this)">
            <div class="news-card-header">
                <span class="news-source">네이버 뉴스</span>
                <span class="news-card-date">${formattedDate}</span>
                ${badgesHtml}
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
    const $header = $card.find('.news-card-header');

    // 화면에서 즉시 읽음 처리
    if (!$card.hasClass('read')) {
        $card.addClass('read');
        // 뱃지가 없으면 추가
        if ($card.find('.news-read-badge').length === 0) {
            $card.find('.news-card-header').append('<span class="news-read-badge">읽음</span>');
        }
        // 전역 변수(readUrls)에도 즉시 추가 (페이지 이동해도 유지되게)
        if (!readUrls.includes(url)) {
            readUrls.push(url);
        }
    }

    // 서버에 읽음 기록 요청
    $.ajax({
        url: `${cpath}/api/mission/news/read`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ newsUrl: url }),
        success: function(data) {
            if (data.earnedPoint && data.earnedPoint > 0) {
                // 이미 포인트 뱃지가 있는지 확인 (중복 추가 방지)
                if ($header.find('.news-points-badge').length === 0) {
                    $header.append(`<span class="news-points-badge">+${data.earnedPoint}P</span>`);
                }
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

function renderPagination() {
    const pagination = $('#newsPagination');
    pagination.empty();

    if (maxPage <= 1) return;

    for (let i = 1; i <= maxPage; i++) {
        const activeClass = (i === currentPage) ? 'active' : '';

        pagination.append(`
            <button class="news-pagination-btn ${activeClass}" onclick="changePage(${i})">
                ${i}
            </button>
        `);
    }
}

function changePage(page) {
    if (page === currentPage) return;
    loadNews(page);
}

// 미션 진행률 로딩
function loadMissionProgress() {
    $.ajax({
        url: `${cpath}/api/mission/news/progress`,
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