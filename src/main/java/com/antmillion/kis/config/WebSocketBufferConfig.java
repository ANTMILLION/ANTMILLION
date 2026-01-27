package com.antmillion.kis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
public class WebSocketBufferConfig {

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container =
                new ServletServerContainerFactoryBean();

        // KIS 데이터는 꽤 큼 → 5MB 이상 권장
        container.setMaxTextMessageBufferSize(5 * 1024 * 1024);
        container.setMaxBinaryMessageBufferSize(5 * 1024 * 1024);

        return container;
    }

}
