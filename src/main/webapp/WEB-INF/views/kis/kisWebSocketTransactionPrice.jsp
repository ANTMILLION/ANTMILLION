<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>실시간 체결 정보</title>
<%@ include file="/WEB-INF/views/common/common.jsp" %>
</head>
<body>

	<div id="stock-panel">
		<h3>
			실시간 체결 정보 (<span id="stock-code">005930</span>)
		</h3>
		<p>
			현재가: <span id="price" style="font-weight: bold;">-</span>
		</p>
		<p>
			전일대비: <span id="vrss">-</span>
		</p>
		<p>
			대비율: <span id="ctrt">-</span>%
		</p>
		<p>
			매수비율: <span id="rate">-</span>%
		</p>
	</div>

	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
		<script> const contextPath = '<%=request.getContextPath()%>'; </script>
		
	<script src="${cpath}/resources/js/kis/kisWebSocketTransactionPrice.js"></script>
</body>
</html>
