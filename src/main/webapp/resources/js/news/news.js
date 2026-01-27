$(document).ready(function() {
    loadNews();
});

function loadNews() {
    $.ajax({
        url: `${cpath}/api/news`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
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

    return `
        <div class="news-card" onclick="openNews('${article.link}')">
            <div class="news-card-header">
                <span class="news-source">네이버 뉴스</span>
                <span class="news-card-date">${formattedDate}</span>
            </div>
            <h2 class="news-card-title">${cleanTitle}</h2>
            <p class="news-card-description">${cleanDescription}</p>
        </div>
    `;
}

function openNews(link) {
    window.open(link, '_blank');
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
            <p class="news-error-message">네트워크 연결을 확인해주세요.</p>
        </div>
    `);
}