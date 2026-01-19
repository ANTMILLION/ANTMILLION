package com.antmillion.kis.config;

import com.antmillion.kis.constant.KisApiConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:kisApi.properties")
@Configuration
public class KisConfig {

    @Value("${kis.app-key}")
    public String appKey;

    @Value("${kis.app-secret}")
    public String appSecret;

    @Value("${kis.account-no}")
    public String accountNo;

    @Value("${kis.acnt-prdt-cd}")
    public String acntPrdtCd;

    @Value("${kis.mode}")
    public String mode;

    /**
     * 실전(real)/모의(virtual) mode에 따라 적절한 Base Url 반환
     */
    public String getBaseUrl() {
        if ("real".equalsIgnoreCase(mode)) {
            return KisApiConstant.REAL_BASE_URL;
        }
        return KisApiConstant.VIRTUAL_BASE_URL;
    }

}
