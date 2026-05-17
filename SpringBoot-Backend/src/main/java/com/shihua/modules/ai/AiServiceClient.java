package com.shihua.modules.ai;

import com.shihua.common.ApiResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class AiServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AiServiceClient.class);

    private final RestClient aiRestClient;
    private final String aiBaseUrl;
    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    public AiServiceClient(RestClient aiRestClient, com.shihua.config.AiProperties aiProperties) {
        this.aiRestClient = aiRestClient;
        this.aiBaseUrl = aiProperties.baseUrl();
    }

    public ApiResponse<?> chat(String sessionId, String message) {
        try {
            Map<String, Object> response = aiRestClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/chat")
                    .queryParam("sessionId", sessionId)
                    .queryParam("message", message)
                    .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("session_id", sessionId, "message", message))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
            if (response == null) {
                return fallbackChat(sessionId);
            }
            return ApiResponse.success(response.getOrDefault("data", response));
        } catch (RestClientException ex) {
            log.warn("Call FastAPI chat failed: {}", ex.getMessage());
            return fallbackChat(sessionId);
        }
    }

    private ApiResponse<Map<String, String>> fallbackChat(String sessionId) {
        return ApiResponse.success(Map.of(
            "reply", "AI客服暂时不可用。你可以先查看鲜花分类、商品详情里的花语和养护指南。",
            "session_id", sessionId,
            "source", "springboot-fallback"
        ));
    }

    public StreamingResponseBody streamChat(String sessionId, String message) {
        return outputStream -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(buildStreamUrl(sessionId, message)))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", MediaType.TEXT_EVENT_STREAM_VALUE)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(toJsonBody(sessionId, message), StandardCharsets.UTF_8))
                    .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (response.statusCode() >= 400) {
                    writeNonStreamChatAsSse(outputStream, sessionId, message);
                    return;
                }
                outputStream.write(response.body().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            } catch (Exception ex) {
                log.warn("Stream FastAPI chat failed: {}", ex.getMessage());
                writeNonStreamChatAsSse(outputStream, sessionId, message);
            }
        };
    }

    private String buildStreamUrl(String sessionId, String message) {
        return aiBaseUrl + "/chat/stream?sessionId=" + encode(sessionId) + "&message=" + encode(message);
    }

    private String toJsonBody(String sessionId, String message) {
        return "{\"session_id\":\"" + jsonEscape(sessionId) + "\",\"message\":\"" + jsonEscape(message) + "\"}";
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String jsonEscape(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n");
    }

    private void writeFallbackStream(OutputStream outputStream, String sessionId) throws java.io.IOException {
        String reply = "AI客服暂时不可用。你可以先查看鲜花分类、商品详情里的花语和养护指南。";
        writeReplyAsSse(outputStream, sessionId, reply, List.of("按预算帮我推荐", "不同花分别代表什么？"), "springboot-fallback");
    }

    private void writeNonStreamChatAsSse(OutputStream outputStream, String sessionId, String message) throws java.io.IOException {
        ApiResponse<?> response = chat(sessionId, message);
        if (response.data() instanceof Map<?, ?> data) {
            Object replyValue = data.get("reply");
            String reply = replyValue == null ? "AI客服暂时不可用。你可以先查看鲜花分类、商品详情里的花语和养护指南。" : String.valueOf(replyValue);
            List<String> suggestions = extractSuggestions(data.get("suggestions"));
            Object sourceValue = data.get("source");
            String source = sourceValue == null ? "springboot-non-stream" : String.valueOf(sourceValue);
            writeReplyAsSse(outputStream, sessionId, reply, suggestions, source);
            return;
        }
        writeFallbackStream(outputStream, sessionId);
    }

    private List<String> extractSuggestions(Object value) {
        if (value instanceof List<?> items) {
            List<String> suggestions = items.stream()
                .limit(2)
                .map(String::valueOf)
                .toList();
            if (suggestions.size() == 2) {
                return suggestions;
            }
        }
        return List.of("按预算帮我推荐", "不同花分别代表什么？");
    }

    private void writeReplyAsSse(OutputStream outputStream, String sessionId, String reply, List<String> suggestions, String source) throws java.io.IOException {
        for (int i = 0; i < reply.length(); i++) {
            String charText = reply.substring(i, i + 1);
            outputStream.write(("event: delta\ndata: {\"event\":\"delta\",\"content\":\"" + jsonEscape(charText) + "\"}\n\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        outputStream.write(("event: done\ndata: {\"event\":\"done\",\"reply\":\"" + jsonEscape(reply)
            + "\",\"session_id\":\"" + jsonEscape(sessionId)
            + "\",\"source\":\"" + jsonEscape(source)
            + "\",\"suggestions\":[\"" + jsonEscape(suggestions.get(0)) + "\",\"" + jsonEscape(suggestions.get(1)) + "\"]}\n\n").getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public ApiResponse<?> generateCard(Map<String, Object> request) {
        try {
            Map<String, Object> response = aiRestClient.post()
                .uri("/generate_card")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
            if (response == null) {
                return ApiResponse.success(Map.of());
            }
            return ApiResponse.success(response.getOrDefault("data", response));
        } catch (RestClientException ex) {
            log.warn("Call FastAPI generate card failed: {}", ex.getMessage());
            return ApiResponse.success(Map.of());
        }
    }
}
