package com.shihua.modules.cart;

import com.shihua.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @GetMapping
    public ApiResponse<List<Object>> list() {
        return ApiResponse.success(List.of());
    }
}

