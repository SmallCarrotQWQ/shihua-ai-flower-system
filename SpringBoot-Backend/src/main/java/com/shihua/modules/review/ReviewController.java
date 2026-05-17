package com.shihua.modules.review;

import com.shihua.common.ApiResponse;
import com.shihua.modules.review.dto.ReviewRequest;
import com.shihua.modules.review.service.ReviewService;
import com.shihua.modules.review.vo.ReviewVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/order/{orderId}")
    public ApiResponse<ReviewVO> create(Authentication authentication, @PathVariable Long orderId, @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(reviewService.create(Long.valueOf(authentication.getName()), orderId, request));
    }

    @GetMapping("/flower/{flowerId}")
    public ApiResponse<List<ReviewVO>> byFlower(@PathVariable Long flowerId) {
        return ApiResponse.success(reviewService.listByFlower(flowerId));
    }
}
