$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const stockCode = urlParams.get('code');
    
    if (!stockCode) {
        console.error('종목 코드가 없습니다.');
        $('#communityListContainer').html('<p>종목 정보를 불러올 수 없습니다.</p>');
        return;
    }
    
    console.log('현재 종목 코드:', stockCode);
    
    loadCommunityList(stockCode);
    
    $('#btnSubmitCommunity').on('click', function() {
        writeCommunity(stockCode);
    });
    
    $('#communityInput').on('keypress', function(e) {
        if (e.which === 13) {
            e.preventDefault();
            writeCommunity(stockCode);
        }
    });
});

function loadCommunityList(stockCode) {
    $.ajax({
        url: (typeof contextPath !== 'undefined' ? contextPath : '/antmillion') + '/community/list',
        type: 'GET',
        data: { stockCode: stockCode },
        success: function(response) {
            console.log('조회 성공:', response);
            if (response.success) {
                displayCommunityList(response.data);
            }
        },
        error: function(xhr, status, error) {
            console.error('AJAX 오류:', error);
            $('#communityListContainer').html('<p>커뮤니티를 불러올 수 없습니다.</p>');
        }
    });
}

function displayCommunityList(communityList) {
    const $container = $('#communityListContainer');
    $container.empty();
    
    if (!communityList || communityList.length === 0) {
        $container.html('<p>아직 댓글이 없습니다.</p>');
        return;
    }
    
    communityList.forEach(function(post) {
        const timeAgo = getTimeAgo(post.postedDate);
        
        const html = `
            <div class="community-post-item">
                <div class="community-post-header">
                    <div class="community-user-info">
                        <span class="community-username">${post.nickname}</span>
                    </div>
                    <span class="community-post-time">${timeAgo}</span>
                </div>
                <div class="community-post-content">
                    ${escapeHtml(post.content)}
                </div>
            </div>
        `;
        
        $container.append(html);
    });
}

function writeCommunity(stockCode) {
    const content = $('#communityInput').val().trim();
    
    if (!content) {
        alert('내용을 입력해주세요.');
        return;
    }
    
    if (content.length > 200) {
        alert('200자 이내로 입력해주세요.');
        return;
    }
    
    $.ajax({
        url: (typeof contextPath !== 'undefined' ? contextPath : '/antmillion') + '/community/write',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            postId: Date.now(),
            userId: 1,
            stockCode: stockCode,
            content: content
        }),
        success: function(response) {
            if (response.success) {
                $('#communityInput').val('');
                loadCommunityList(stockCode);
            } else {
                alert('작성 실패: ' + response.message);
            }
        },
        error: function(xhr, status, error) {
            alert('작성 중 오류가 발생했습니다.');
        }
    });
}

function getTimeAgo(dateTimeStr) {
    let posted;
    
    if (Array.isArray(dateTimeStr)) {
        posted = new Date(dateTimeStr[0], dateTimeStr[1] - 1, dateTimeStr[2], 
                         dateTimeStr[3] || 0, dateTimeStr[4] || 0, dateTimeStr[5] || 0);
    } else {
        posted = new Date(dateTimeStr);
    }
    
    const now = new Date();
    const diffMs = now - posted;
    const diffMin = Math.floor(diffMs / 1000 / 60);
    const diffHour = Math.floor(diffMin / 60);
    const diffDay = Math.floor(diffHour / 24);
    
    if (diffMin < 1) return '방금 전';
    if (diffMin < 60) return diffMin + '분 전';
    if (diffHour < 24) return diffHour + '시간 전';
    if (diffDay < 7) return diffDay + '일 전';
    
    const year = posted.getFullYear();
    const month = String(posted.getMonth() + 1).padStart(2, '0');
    const day = String(posted.getDate()).padStart(2, '0');
    return year + '.' + month + '.' + day;
}

function escapeHtml(text) {
    const map = {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;'};
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}