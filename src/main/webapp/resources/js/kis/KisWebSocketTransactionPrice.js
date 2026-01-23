var stompClient = null;

function connect() {
    // 1. Spring Config에서 설정한 Endpoint 주소 (예: /ws-stomp)
    var socket = new SockJS('/ws-stomp'); 
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // 2. 백엔드에서 쏘는 Topic 주소를 구독
        stompClient.subscribe('/topic/kis-trade', function (response) {
            // 전달받은 데이터를 JSON으로 파싱
            var tradeData = JSON.parse(response.body);
            
            // 화면 갱신 함수 호출
            updateUI(tradeData);
        });
    }, function (error) {
        console.error('STOMP error: ' + error);
        // 연결 끊기면 5초 후 재시도
        setTimeout(connect, 5000);
    });
}

function updateUI(data) {
    document.getElementById('stock-code').innerText = data.mkscShrnIscd;
    document.getElementById('price').innerText = data.stckPrpr;
    document.getElementById('vrss').innerText = data.prdyVrss;
    document.getElementById('ctrt').innerText = data.prdyCtrt;
    document.getElementById('rate').innerText = data.shnuRate;

    // 가격 등락에 따른 색상 변경 로직 (꿀팁)
    var priceTag = document.getElementById('price');
    if (parseFloat(data.prdyCtrt) > 0) {
        priceTag.style.color = 'red';
    } else if (parseFloat(data.prdyCtrt) < 0) {
        priceTag.style.color = 'blue';
    } else {
        priceTag.style.color = 'black';
    }
}

// 페이지 로드 시 연결 시작
window.onload = function() {
    connect();
};