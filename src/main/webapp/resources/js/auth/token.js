(function () {
  // 1) token.js가 여러 번 로드되어도 "한 번만" 초기화되도록 가드
  if (window.__ANT_TOKEN_BOOTSTRAPPED__) return;
  window.__ANT_TOKEN_BOOTSTRAPPED__ = true;

  const TOKEN_KEY = 'AT';

  // 2) contextPath는 window 전역을 우선 사용(없으면 기존 contextPath fallback)
  const base =
    (typeof window.contextPath === 'string' ? window.contextPath :
     (typeof contextPath === 'string' ? contextPath : ''));
  const REFRESH_ENDPOINT = base + '/auth/refresh';

  // 원본 fetch는 한 번만 잡아둠(래핑된 fetch로 refresh 재귀 호출 방지)
  const _fetch = window.fetch.bind(window);

  window.getAccessToken = function () {
    return sessionStorage.getItem(TOKEN_KEY);
  };

  window.setAccessToken = function (token) {
    if (!token) return;
    sessionStorage.setItem(TOKEN_KEY, token);
  };

  window.clearAccessToken = function () {
    sessionStorage.removeItem(TOKEN_KEY);
  };

  // 3) refresh가 동시에 여러 번 호출되지 않도록 "single-flight" 락
  let refreshPromise = null;

  async function refreshAccessToken() {
    if (refreshPromise) return refreshPromise;

    refreshPromise = (async () => {
      const res = await _fetch(REFRESH_ENDPOINT, {
        method: 'POST',
        credentials: 'same-origin'
      });

      if (!res.ok) {
        // 401(= NO_REFRESH / REFRESH_MISMATCH)일 때만 AT 제거 (깜빡임/오판 줄이기)
        if (res.status === 401) clearAccessToken();
        return null;
      }

      const data = await res.json().catch(() => null);
      const token = data && data.accessToken ? data.accessToken : null;
      if (token) setAccessToken(token);
      return token;
    })();

    try {
      return await refreshPromise;
    } finally {
      refreshPromise = null;
    }
  }

  // fetch 래퍼: 같은 오리진 요청에는 자동으로 Authorization 헤더 부착
  window.fetch = async function (input, init) {
    const opts = init ? { ...init } : {};
    const headers = new Headers(opts.headers || {});

    const url = typeof input === 'string' ? input : (input && input.url ? input.url : '');
    const isRefreshCall = url.includes('/auth/refresh');

    // Authorization이 없고, 토큰이 있으면 붙임
    if (!headers.has('Authorization')) {
      const at = getAccessToken();
      if (at && !isRefreshCall) {
        headers.set('Authorization', 'Bearer ' + at);
      }
    }

    opts.headers = headers;

    if (!opts.credentials) {
      opts.credentials = 'same-origin';
    }

    let res = await _fetch(input, opts);

    // 401이면 RT로 AT 재발급 시도 후 1회 재시도
    if (res.status === 401 && !opts.__retried && !isRefreshCall) {
      const newToken = await refreshAccessToken();
      if (newToken) {
        const retryOpts = { ...opts, __retried: true };
        const retryHeaders = new Headers(retryOpts.headers || {});
        retryHeaders.set('Authorization', 'Bearer ' + newToken);
        retryOpts.headers = retryHeaders;
        res = await _fetch(input, retryOpts);
      }
    }

    return res;
  };

  document.addEventListener('DOMContentLoaded', async () => {
    if (!getAccessToken()) {
      await refreshAccessToken();
    }

    // jQuery $.ajax 도 Authorization 자동 부착
    if (window.jQuery && window.jQuery.ajaxSetup) {
      window.jQuery.ajaxSetup({
        beforeSend: function (xhr) {
          const at = getAccessToken();
          if (at) {
            xhr.setRequestHeader('Authorization', 'Bearer ' + at);
          }
        }
      });
    }
  });
})();
