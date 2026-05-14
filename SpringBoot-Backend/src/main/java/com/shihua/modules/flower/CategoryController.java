package com.shihua.modules.flower;

import com.shihua.common.ApiResponse;
import com.shihua.modules.flower.service.FlowerService;
import com.shihua.modules.flower.vo.CategoryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final FlowerService flowerService;

    public CategoryController(FlowerService flowerService) {
        this.flowerService = flowerService;
    }

    @GetMapping
    public ApiResponse<List<CategoryVO>> list() {
        return ApiResponse.success(flowerService.listCategories());
    }
}

