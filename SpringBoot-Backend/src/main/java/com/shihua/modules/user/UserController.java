package com.shihua.modules.user;

import com.shihua.common.ApiResponse;
import com.shihua.modules.user.dto.AddressRequest;
import com.shihua.modules.user.dto.ChangePasswordRequest;
import com.shihua.modules.user.dto.LoginRequest;
import com.shihua.modules.user.dto.RegisterRequest;
import com.shihua.modules.user.dto.UpdateProfileRequest;
import com.shihua.modules.user.service.AddressService;
import com.shihua.modules.user.service.UserService;
import com.shihua.modules.user.vo.AddressVO;
import com.shihua.modules.user.vo.LoginVO;
import com.shihua.modules.user.vo.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
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
        return ApiResponse.success(userService.getById(currentUserId(authentication)));
    }

    @PutMapping("/info")
    public ApiResponse<UserInfoVO> updateProfile(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success(userService.updateProfile(currentUserId(authentication), request));
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUserId(authentication), request);
        return ApiResponse.success(null);
    }

    @GetMapping("/address")
    public ApiResponse<List<AddressVO>> addresses(Authentication authentication) {
        return ApiResponse.success(addressService.list(currentUserId(authentication)));
    }

    @PostMapping("/address")
    public ApiResponse<AddressVO> createAddress(Authentication authentication, @Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(addressService.create(currentUserId(authentication), request));
    }

    @PutMapping("/address/{id}")
    public ApiResponse<AddressVO> updateAddress(Authentication authentication, @PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(addressService.update(currentUserId(authentication), id, request));
    }

    @DeleteMapping("/address/{id}")
    public ApiResponse<Void> deleteAddress(Authentication authentication, @PathVariable Long id) {
        addressService.delete(currentUserId(authentication), id);
        return ApiResponse.success(null);
    }

    @PutMapping("/address/{id}/default")
    public ApiResponse<Void> defaultAddress(Authentication authentication, @PathVariable Long id) {
        addressService.setDefault(currentUserId(authentication), id);
        return ApiResponse.success(null);
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
