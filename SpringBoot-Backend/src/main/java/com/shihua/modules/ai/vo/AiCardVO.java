package com.shihua.modules.ai.vo;

import java.time.LocalDateTime;

public record AiCardVO(
    Long cardId,
    Long flowerId,
    String relation,
    String occasion,
    String generatedContent,
    LocalDateTime createTime
) {
}
