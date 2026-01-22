package com.antmillion.history.dto;


public enum BiasType {
    
    /**
     * 안전선호 오류
     * - 조건: 수익률 >= 3% AND 보유 기간 <= 3일
     * - 증상: 작은 수익에 만족하고 빨리 매도
     */
    SAFE_HAVEN("SAFE_HAVEN", "안전선호 오류", "작은 수익에 만족하여 성급하게 매도하려고 합니다"),
    
    /**
     * 손실회피 편향
     * - 조건: 수익률 < -10% AND 보유 기간 > 30일
     * - 증상: 손실 인정 못하고 계속 보유
     */
    LOSS_AVERSION("LOSS_AVERSION", "손실회피 편향", "손실을 인정하지 못하고 보유를 지속하고 있습니다"),
    
    /**
     * 확증 편향
     * - 조건: 같은 종목 반복 매수
     * - 증상: 자신의 판단만 믿고 반복 매수
     */
    CONFIRMATION_BIAS("CONFIRMATION_BIAS", "확증 편향", "자신의 판단만 믿고 같은 종목을 반복 매수하고 있습니다"),
    
    /**
     * 매몰비용 오류
     * - 조건: 손실 중인데 추가 매수
     * - 증상: 이미 투자한 돈이 아까워서 추가 매수
     */
    SUNK_COST("SUNK_COST", "매몰비용 오류", "이미 투자한 금액 때문에 추가 매수를 고려하고 있습니다"),
    
    /**
     * FOMO (Fear Of Missing Out)
     * - 조건: 급등주 매수
     * - 증상: 놓칠까봐 두려워서 고점 매수
     */
    FOMO("FOMO", "FOMO 편향", "급등한 종목에 대한 두려움으로 고점 매수를 시도하고 있습니다");
    
    private final String code;
    private final String title;
    private final String message;
    
    BiasType(String code, String title, String message) {
        this.code = code;
        this.title = title;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * code로 BiasType 찾기
     */
    public static BiasType fromCode(String code) {
        for (BiasType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown bias type code: " + code);
    }
    
    /**
     * code로 BiasType 찾기 (null 안전)
     */
    public static BiasType fromCodeOrNull(String code) {
        if (code == null) return null;
        for (BiasType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}