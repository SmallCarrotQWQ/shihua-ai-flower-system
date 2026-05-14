package com.shihua.modules.flower;

import com.shihua.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flower")
public class FlowerController {

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.success(List.of(
            Map.of("flowerId", 1001, "flowerName", "玫瑰", "price", new BigDecimal("99.00")),
            Map.of("flowerId", 1002, "flowerName", "百合", "price", new BigDecimal("89.00"))
        ));
    }

    @GetMapping("/hot")
    public ApiResponse<List<String>> hot() {
        return ApiResponse.success(List.of("玫瑰", "康乃馨", "百合"));
    }
}

