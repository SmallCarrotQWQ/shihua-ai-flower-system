package com.shihua.modules.order.vo;

import java.math.BigDecimal;

public record OrderItemVO(
    Long itemId,
    Long flowerId,
    String flowerName,
    BigDecimal price,
    Integer quantity,
    BigDecimal subtotal
) {
}

