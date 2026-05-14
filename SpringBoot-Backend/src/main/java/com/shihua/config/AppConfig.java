package com.shihua.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({AiProperties.class, JwtProperties.class})
public class AppConfig {

    @Bean
    public RestClient aiRestClient(AiProperties properties) {
        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .build();
    }
}

