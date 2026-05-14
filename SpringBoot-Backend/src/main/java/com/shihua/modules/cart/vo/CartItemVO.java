package com.shihua.modules.cart.vo;

import java.math.BigDecimal;

public record CartItemVO(
    Long cartId,
    Long flowerId,
    String flowerName,
    BigDecimal price,
    Integer stock,
    String coverImage,
    Integer quantity,
    BigDecimal subtotal
) {
}

