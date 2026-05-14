package com.shihua.modules.flower;

import com.shihua.common.ApiResponse;
import com.shihua.modules.flower.service.FlowerService;
import com.shihua.modules.flower.vo.FlowerVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/flower")
public class FlowerController {

    private final FlowerService flowerService;

    public FlowerController(FlowerService flowerService) {
        this.flowerService = flowerService;
    }

    @GetMapping
    public ApiResponse<List<FlowerVO>> list(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.success(flowerService.listFlowers(categoryId, keyword));
    }

    @GetMapping("/hot")
    public ApiResponse<List<FlowerVO>> hot(@RequestParam(required = false) Integer limit) {
        return ApiResponse.success(flowerService.hotFlowers(limit));
    }

    @GetMapping("/{id}")
    public ApiResponse<FlowerVO> detail(@PathVariable("id") Long id) {
        return ApiResponse.success(flowerService.detail(id));
    }
}
