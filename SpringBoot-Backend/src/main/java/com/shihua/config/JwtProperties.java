package com.shihua.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shihua.jwt")
public record JwtProperties(String secret, long ttlSeconds) {
}

