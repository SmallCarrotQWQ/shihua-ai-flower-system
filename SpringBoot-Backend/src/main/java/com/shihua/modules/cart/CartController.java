package com.shihua.modules.cart;

import com.shihua.common.ApiResponse;
import com.shihua.modules.cart.dto.AddCartRequest;
import com.shihua.modules.cart.dto.UpdateCartRequest;
import com.shihua.modules.cart.service.CartService;
import com.shihua.modules.cart.vo.CartItemVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<List<CartItemVO>> list(Authentication authentication) {
        return ApiResponse.success(cartService.list(currentUserId(authentication)));
    }

    @PostMapping
    public ApiResponse<List<CartItemVO>> add(Authentication authentication, @Valid @RequestBody AddCartRequest request) {
        return ApiResponse.success(cartService.add(currentUserId(authentication), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<List<CartItemVO>> update(
        Authentication authentication,
        @PathVariable("id") Long id,
        @Valid @RequestBody UpdateCartRequest request
    ) {
        return ApiResponse.success(cartService.update(currentUserId(authentication), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<List<CartItemVO>> delete(Authentication authentication, @PathVariable("id") Long id) {
        return ApiResponse.success(cartService.delete(currentUserId(authentication), id));
    }

    @DeleteMapping
    public ApiResponse<Void> clear(Authentication authentication) {
        cartService.clear(currentUserId(authentication));
        return ApiResponse.success(null);
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
