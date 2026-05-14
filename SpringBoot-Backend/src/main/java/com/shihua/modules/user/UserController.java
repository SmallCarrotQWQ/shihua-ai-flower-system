package com.shihua.modules.user;

import com.shihua.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/info")
    public ApiResponse<Map<String, String>> info() {
        return ApiResponse.success(Map.of("username", "demo", "role", "USER"));
    }
}

