(function () {
  if (window.__ANT_TOKEN_BOOTSTRAPPED__) return;
  window.__ANT_TOKEN_BOOTSTRAPPED__ = true;

  const base =
    (typeof window.contextPath === 'string' ? window.contextPath :
     (typeof contextPath === 'string' ? contextPath : ''));

  const REFRESH_ENDPOINT = base + '/auth/refresh';
  const _fetch = window.fetch.bind(window);

  window.getAccessToken = function () { return null; };
  window.setAccessToken = function () {};
  window.clearAccessToken = function () {};

  let refreshPromise = null;

  async function refreshCookies() {
    if (refreshPromise) return refreshPromise;

    refreshPromise = (async () => {
      const res = await _fetch(REFRESH_ENDPOINT, {
        method: 'POST',
        credentials: 'same-origin'
      });

      if (!res.ok) {
        // RT 만료/불일치 => 로그인으로
        if (res.status === 401) {
          const cur = window.location.pathname || '';
          const loginPath = base + '/login';
          const signupPath = base + '/signup';
          if (!cur.startsWith(loginPath) && !cur.startsWith(signupPath)) {
            window.location.replace(loginPath);
          }
        }
        return false;
      }
      return true;
    })();

    try {
      return await refreshPromise;
    } finally {
      refreshPromise = null;
    }
  }

  window.fetch = async function (input, init) {
    const opts = init ? { ...init } : {};
    const url = typeof input === 'string' ? input : (input && input.url ? input.url : '');
    const isRefreshCall = url.includes('/auth/refresh');

    if (!opts.credentials) {
      opts.credentials = 'same-origin'; // 쿠키 포함
    }

    opts.headers = opts.headers || {};

    let res = await _fetch(input, opts);

    // 401이면 RT로 쿠키(AT/RT) 재발급 후 1회 재시도
    if (res.status === 401 && !opts.__retried && !isRefreshCall) {
      const ok = await refreshCookies();
      if (ok) {
        const retryOpts = { ...opts, __retried: true };
        res = await _fetch(input, retryOpts);
      }
    }

    return res;
  };

  // jQuery는 same-origin이면 기본적으로 쿠키가 실림(헤더 주입 제거)
})();
