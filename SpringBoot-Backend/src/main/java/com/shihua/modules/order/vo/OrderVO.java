package com.shihua.modules.order.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderVO(
    Long orderId,
    BigDecimal totalAmount,
    Integer status,
    Long addressId,
    String remark,
    LocalDateTime createTime,
    List<OrderItemVO> items
) {
}

