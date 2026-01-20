package com.antmillion.kis.constant;

public class KisApiConstant {

    public static final String REAL_BASE_URL = "https://openapi.koreainvestment.com:9443";
    public static final String VIRTUAL_BASE_URL = "https://openapivts.koreainvestment.com:29443";

    public static final String OAUTH_TOKEN_PATH = "/oauth2/tokenP";
    public static final String PERIOD_PRICE_PATH = "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice";
    public static final String MARKET_INDEX_PATH = "/uapi/domestic-stock/v1/quotations/inquire-index-daily-price";    
    public static final String STREAM_MINUTE_PATH = "/uapi/domestic-stock/v1/quotations/inquire-time-dailychartprice";
    public static final String STOCK_VOLUME_RANK = "/uapi/domestic-stock/v1/quotations/volume-rank";
    public static final String DAY_MINUTE_PATH = "/uapi/domestic-stock/v1/quotations/inquire-time-itemchartprice";
    private KisApiConstant() {
        // 인스턴스화 방지
    }

}
