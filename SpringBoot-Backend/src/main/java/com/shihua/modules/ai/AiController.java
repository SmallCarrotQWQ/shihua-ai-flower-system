package com.shihua.modules.ai;

import com.shihua.common.ApiResponse;
import com.shihua.modules.ai.dto.AiChatRequest;
import com.shihua.modules.ai.dto.GenerateCardRequest;
import com.shihua.modules.ai.vo.AiCardVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiServiceClient aiServiceClient;
    private final AiCardService aiCardService;

    public AiController(AiServiceClient aiServiceClient, AiCardService aiCardService) {
        this.aiServiceClient = aiServiceClient;
        this.aiCardService = aiCardService;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("module", "ai", "status", "ready"));
    }

    @PostMapping("/chat")
    public ApiResponse<?> chat(@Valid @RequestBody AiChatRequest request, Authentication authentication) {
        String sessionId = resolveSessionId(request.getSessionId(), authentication);
        return aiServiceClient.chat(sessionId, request.getMessage());
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> streamChat(@Valid @RequestBody AiChatRequest request, Authentication authentication) {
        String sessionId = resolveSessionId(request.getSessionId(), authentication);
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(aiServiceClient.streamChat(sessionId, request.getMessage()));
    }

    @PostMapping("/card")
    public ApiResponse<AiCardVO> generateCard(@Valid @RequestBody GenerateCardRequest request, Authentication authentication) {
        return ApiResponse.success(aiCardService.generate(currentUserId(authentication), request));
    }

    @GetMapping("/card")
    public ApiResponse<List<AiCardVO>> cards(Authentication authentication) {
        return ApiResponse.success(aiCardService.list(currentUserId(authentication)));
    }

    private String resolveSessionId(String requestSessionId, Authentication authentication) {
        if (requestSessionId != null && !requestSessionId.isBlank()) {
            return requestSessionId;
        }
        if (authentication != null && authentication.isAuthenticated()) {
            return "user_" + authentication.getName();
        }
        return "guest_" + UUID.randomUUID();
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
