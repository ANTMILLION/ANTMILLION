package com.antmillion.history.dto;


public enum BiasType {
    
    /**
     * 위험회피 편향
     * - 조건: 수익률 <= 3%
     * - 증상: 작은 수익에 만족하고 빨리 매도
     */
    RISK_AVERSION("RISK_AVERSION", "위험회피 주의", "해당 종목 수익률이 +3% 이하입니다.\r\n"
    		+ "잦은 매매는 거래비용을 증가시켜 수익성을 악화시킬 수 있습니다.\r\n"
    		+ "지금의 매도 판단에 대한 확실한 근거가 없다면 단순히 위험회피적 판단은 아닌지 다시 한번 점검해 보세요."),
    
    /**
     * 손실회피 편향
     * - 조건: 수익률 <= -7%
     * - 증상: 손실 인정 못하고 계속 보유
     */
    LOSS_AVERSION("LOSS_AVERSION", "손실회피 주의", "해당 종목 수익률이 -7% 이하이고 최근 5거래일간 매도 이력이 없습니다.\r\n"
    		+ "손실이 커질수록 원금 회복에 필요한 수익률은 기하급수적으로 증가합니다. 빠르게 손실을 확정하는 것이 자산을 지키는 선택일 수 있습니다.\r\n"
    		+ "지금의 보유 판단에 대한 확실한 근거가 없다면 단순히 손실회피적 판단은 아닌지 다시 한번 점검해 보세요."),
    
    /**
     * 매몰비용 오류
     * - 조건: 수익률 <= -15% AND 보유기간 >= 21일
     * - 증상: 이미 투자한 돈이 아까워서 계속 보유 또는 추가 매수
     */
    SUNK_COST("SUNK_COST", "매몰비용오류 경고", "해당 종목 수익률이 -15% 이하이고 21일 이상 보유 중입니다.\r\n"
    		+ "지금의 보유 또는 매수 판단에 대한 확실한 근거가 없다면 매몰비용을 고려한 판단일 수 있습니다.\r\n"
    		+ "매몰비용에 대한 현재 및 미래의 경제적 가치는 0원이므로 의사결정에 매몰비용이 고려되지 않아야 합니다.\r\n"
    		+ "현재의 판단이 과거의 투자에 묶여 있는 것은 아닌지 다시 한번 점검해 보세요."),
    
    /**
     * FOMO (Fear Of Missing Out)
     * - 조건: 급등주 매수 (+20%)
     * - 증상: 놓칠까봐 두려워서 고점 매수
     */
    FOMO("FOMO", "FOMO 주의", "급등 뒤에 오는 조정은 더 큰 고통을 줄 수 있습니다. 지금의 매수 판단이 철저한 분석인지, 아니면 단순히 소외되지 않으려는 본능적인 공포인지 다시 한번 점검해 보세요.");
    
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