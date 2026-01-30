// ===============================
// ✅ 로그인 필요 모달 유틸 (community.js)
// - detail.js가 페이지 아래에서 로드될 수 있어서 여기서도 보장
// ===============================
(function () {
    function isLoggedIn() {
        try {
            return (typeof IS_LOGGED_IN !== 'undefined') ? !!IS_LOGGED_IN : false;
        } catch (e) {
            return false;
        }
    }

    if (typeof window.__isLoggedIn !== 'function') {
        window.__isLoggedIn = isLoggedIn;
    }

    if (typeof window.ensureLoginRequiredModal !== 'function') {
        window.ensureLoginRequiredModal = function () {
            if (document.getElementById('loginRequiredModal')) return;

            const modal = document.createElement('div');
            modal.id = 'loginRequiredModal';
            modal.className = 'login-required-modal';
            modal.setAttribute('aria-hidden', 'true');

            modal.innerHTML = `
                <div class="login-required-modal__backdrop" data-close="true"></div>
                <div class="login-required-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="loginRequiredTitle">
                    <h4 id="loginRequiredTitle" class="login-required-modal__title">로그인이 필요합니다</h4>
                    <button type="button" class="login-required-modal__btn" id="loginRequiredClose">확인</button>
                </div>
            `;

            document.body.appendChild(modal);

            const close = () => {
                modal.classList.remove('active');
                modal.setAttribute('aria-hidden', 'true');
            };

            const open = () => {
                modal.classList.add('active');
                modal.setAttribute('aria-hidden', 'false');
            };

            if (typeof window.openLoginRequiredModal !== 'function') {
                window.openLoginRequiredModal = open;
            }
            if (typeof window.closeLoginRequiredModal !== 'function') {
                window.closeLoginRequiredModal = close;
            }

            const closeBtn = document.getElementById('loginRequiredClose');
            if (closeBtn) closeBtn.addEventListener('click', close);

            modal.addEventListener('click', (e) => {
                if (e.target && e.target.dataset && e.target.dataset.close === 'true') close();
            });

            document.addEventListener('keydown', (e) => {
                if (e.key === 'Escape') close();
            });
        };
    }

    if (typeof window.requireLogin !== 'function') {
        window.requireLogin = function (e) {
            if (window.__isLoggedIn && window.__isLoggedIn()) return true;

            if (e) {
                e.preventDefault?.();
                e.stopImmediatePropagation?.();
                e.stopPropagation?.();
            }

            window.ensureLoginRequiredModal?.();
            window.openLoginRequiredModal ? window.openLoginRequiredModal() : alert('로그인이 필요합니다');
            return false;
        };
    }
})();

$(document).ready(function() {
    // 비로그인: 커뮤니티 이용 불가 + 모달
    if (typeof window.__isLoggedIn === 'function' && !window.__isLoggedIn()) {
        // 목록 영역: 로그인 안내로 대체
        $('#communityListContainer').html('<div class="community-login-required">로그인이 필요합니다</div>');

        // 입력 막기(클릭은 허용해서 모달 띄움)
        $('#communityInput')
            .val('')
            .attr('readonly', true)
            .attr('placeholder', '로그인이 필요합니다')
            .on('focus click keydown', function(e) {
                window.requireLogin && window.requireLogin(e);
            });

        // 전송 버튼도 모달
        $('#btnSubmitCommunity').on('click', function(e) {
            window.requireLogin && window.requireLogin(e);
        });

        // 커뮤니티 영역 클릭 시도도 모달(선택)
        $('.community-content').on('click', function(e) {
            // 입력/버튼에서 이미 처리하지만, 빈 영역 클릭도 모달 띄우고 싶으면 유지
            window.requireLogin && window.requireLogin(e);
        });

        return; // 비로그인: 아래 로직 실행 X
    }

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
    // ⭐ 수정: contextPath에 / 추가 (절대 경로로 만들기)
    const basePath = (typeof contextPath !== 'undefined' && contextPath) 
        ? (contextPath.startsWith('/') ? contextPath : '/' + contextPath)
        : '/antmillion';
    
    console.log('[커뮤니티] basePath:', basePath);
    const apiUrl = basePath + '/community/list';
    console.log('[커뮤니티] API URL:', apiUrl);
    
    $.ajax({
        url: apiUrl,
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
            console.error('상태 코드:', xhr.status);
            console.error('응답:', xhr.responseText);
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
    
    // ⭐ 수정: contextPath에 / 추가 (절대 경로로 만들기)
    const basePath = (typeof contextPath !== 'undefined' && contextPath) 
        ? (contextPath.startsWith('/') ? contextPath : '/' + contextPath)
        : '/antmillion';
    
    $.ajax({
        url: basePath + '/community/write',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            postId: Date.now(),
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
