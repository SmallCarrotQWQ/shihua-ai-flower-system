package com.shihua.modules.flower.vo;

import java.math.BigDecimal;

public record FlowerVO(
    Long flowerId,
    String flowerName,
    Long categoryId,
    String categoryName,
    BigDecimal price,
    Integer stock,
    String coverImage,
    String description,
    String flowerLanguage,
    String careGuide,
    Integer salesCount,
    Integer status
) {
}
