package com.shihua.modules.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddCartRequest {

    @NotNull(message = "鲜花ID不能为空")
    private Long flowerId;

    @Min(value = 1, message = "数量至少为1")
    private Integer quantity = 1;

    public Long getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(Long flowerId) {
        this.flowerId = flowerId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

