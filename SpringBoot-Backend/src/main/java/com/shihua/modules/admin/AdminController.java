package com.shihua.modules.admin;

import com.shihua.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard/stats")
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.success(Map.of("ordersToday", 0, "salesAmount", 0, "newUsers", 0));
    }
}

