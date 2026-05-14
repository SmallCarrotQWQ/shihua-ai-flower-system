package com.shihua.modules.order;

import com.shihua.common.ApiResponse;
import com.shihua.modules.order.dto.CreateOrderRequest;
import com.shihua.modules.order.service.OrderService;
import com.shihua.modules.order.vo.OrderVO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderVO> create(Authentication authentication, @RequestBody CreateOrderRequest request) {
        if (request == null) {
            request = new CreateOrderRequest();
        }
        return ApiResponse.success(orderService.create(currentUserId(authentication), request));
    }

    @GetMapping
    public ApiResponse<List<OrderVO>> list(Authentication authentication) {
        return ApiResponse.success(orderService.list(currentUserId(authentication)));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderVO> detail(Authentication authentication, @PathVariable("id") Long id) {
        return ApiResponse.success(orderService.detail(currentUserId(authentication), id));
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
