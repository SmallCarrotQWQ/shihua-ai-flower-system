package com.shihua.modules.user;

import com.shihua.common.ApiResponse;
import com.shihua.modules.user.dto.LoginRequest;
import com.shihua.modules.user.dto.RegisterRequest;
import com.shihua.modules.user.service.UserService;
import com.shihua.modules.user.vo.LoginVO;
import com.shihua.modules.user.vo.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @GetMapping("/info")
    public ApiResponse<UserInfoVO> info(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ApiResponse.success(userService.getById(userId));
    }
}
