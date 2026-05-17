package com.shihua.modules.review.vo;

import java.time.LocalDateTime;

public record ReviewVO(
    Long reviewId,
    Long userId,
    Long flowerId,
    Long orderId,
    Integer rating,
    String content,
    String sentiment,
    String keywords,
    LocalDateTime createTime
) {
}
