package com.antmillion.kis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:kisApi.properties")
@Configuration
public class KisConfig {

    @Value("${kis.base-url}")
    public String baseUrl;

    @Value("${kis.app-key}")
    public String appKey;

    @Value("${kis.app-secret}")
    public String appSecret;

    @Value("${kis.account-no}")
    public String accountNo;

    @Value("${kis.acnt-prdt-cd}")
    public String acntPrdtCd;

}
