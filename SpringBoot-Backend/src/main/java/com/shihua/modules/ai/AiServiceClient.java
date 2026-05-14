package com.shihua.modules.ai;

import com.shihua.common.ApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class AiServiceClient {

    private final RestClient aiRestClient;

    public AiServiceClient(RestClient aiRestClient) {
        this.aiRestClient = aiRestClient;
    }

    public ApiResponse<?> chat(String sessionId, String message) {
        return aiRestClient.post()
            .uri("/chat")
            .body(Map.of("session_id", sessionId, "message", message))
            .retrieve()
            .body(ApiResponse.class);
    }

    public ApiResponse<?> generateCard(Map<String, Object> request) {
        return aiRestClient.post()
            .uri("/generate_card")
            .body(request)
            .retrieve()
            .body(ApiResponse.class);
    }
}

