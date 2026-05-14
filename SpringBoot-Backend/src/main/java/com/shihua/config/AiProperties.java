package com.shihua.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shihua.ai")
public record AiProperties(String baseUrl) {
}

