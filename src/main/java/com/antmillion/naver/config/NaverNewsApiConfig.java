package com.antmillion.naver.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@PropertySource("classpath:naverNewsApi.properties")
@Configuration
public class NaverNewsApiConfig {
    @Value("${naverNews.api.client-id}")
    private String clientId;

    @Value("${naverNews.api.client-secret}")
    private String clientSecret;

    @Value("${naverNews.api.news-url}")
    private String newsUrl;
}
