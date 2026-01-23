var stompClient = null;

function connect() {
    // 1. Spring Config에서 설정한 Endpoint 주소 (예: /ws-stomp)
    var url = contextPath + '/ws-stomp';
    console.log("연결 시도 주소: " + url); // 브라우저 콘솔에서 주소 맞는지 확인용
    
    var socket = new SockJS(url);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // 2. 백엔드에서 쏘는 Topic 주소를 구독
        stompClient.subscribe('/topic/kis-trade/' + currentStockCode, function (response) {

            // 전달받은 데이터를 JSON으로 파싱
            var tradeData = JSON.parse(response.body);
                console.log("수신 데이터:", tradeData);
            // 화면 갱신 함수 호출
            updateUI(tradeData);
        });
    });
}

function updateUI(data) {
    document.getElementById('stock-code').innerText = data.mkscShrnIscd;
    document.getElementById('price').innerText = data.stckPrpr;
    document.getElementById('vrss').innerText = data.prdyVrss;
    document.getElementById('ctrt').innerText = data.prdyCtrt;
    document.getElementById('rate').innerText = data.shnuRate;

    // 가격 등락에 따른 색상 변경 로직
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
document.addEventListener("DOMContentLoaded", function() {
    console.log("DOM 로드 완료, 연결 시작!");
});
