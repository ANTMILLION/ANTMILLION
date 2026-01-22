package com.antmillion.kis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.antmillion.kis.constant.KisApiConstant;

@PropertySource("classpath:kisApi.properties")
@Configuration
public class KisWebSocketConfig {

    @Value("${kis.app-key}")
    public String appKey;

    @Value("${kis.secret-key}")
    public String secretKey;

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
