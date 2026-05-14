package com.shihua.common;

import com.shihua.config.AiProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final AiProperties aiProperties;
    private final RedisConnectionFactory redisConnectionFactory;

    public HealthController(AiProperties aiProperties, RedisConnectionFactory redisConnectionFactory) {
        this.aiProperties = aiProperties;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(Map.of(
            "service", "SpringBoot-Backend",
            "status", "UP",
            "fastApiBaseUrl", aiProperties.baseUrl(),
            "redisConfigured", redisConnectionFactory != null
        ));
    }
}

