package com.shihua.modules.admin;

import com.shihua.common.ApiResponse;
import com.shihua.modules.admin.dto.AdminCategoryRequest;
import com.shihua.modules.admin.dto.AdminFlowerRequest;
import com.shihua.modules.admin.dto.StatusRequest;
import com.shihua.modules.admin.service.AdminService;
import com.shihua.modules.flower.vo.CategoryVO;
import com.shihua.modules.flower.vo.FlowerVO;
import com.shihua.modules.order.vo.OrderVO;
import com.shihua.modules.user.vo.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard/stats")
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.success(adminService.dashboardStats());
    }

    @GetMapping("/flower")
    public ApiResponse<List<FlowerVO>> flowers() {
        return ApiResponse.success(adminService.listFlowers());
    }

    @PostMapping("/flower")
    public ApiResponse<FlowerVO> createFlower(@Valid @RequestBody AdminFlowerRequest request) {
        return ApiResponse.success(adminService.createFlower(request));
    }

    @PutMapping("/flower/{id}")
    public ApiResponse<FlowerVO> updateFlower(@PathVariable Long id, @Valid @RequestBody AdminFlowerRequest request) {
        return ApiResponse.success(adminService.updateFlower(id, request));
    }

    @PutMapping("/flower/{id}/status")
    public ApiResponse<Void> updateFlowerStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        adminService.updateFlowerStatus(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/flower/{id}")
    public ApiResponse<Void> deleteFlower(@PathVariable Long id) {
        adminService.deleteFlower(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/category")
    public ApiResponse<List<CategoryVO>> categories() {
        return ApiResponse.success(adminService.listCategories());
    }

    @PostMapping("/category")
    public ApiResponse<CategoryVO> createCategory(@Valid @RequestBody AdminCategoryRequest request) {
        return ApiResponse.success(adminService.createCategory(request));
    }

    @PutMapping("/category/{id}")
    public ApiResponse<CategoryVO> updateCategory(@PathVariable Long id, @Valid @RequestBody AdminCategoryRequest request) {
        return ApiResponse.success(adminService.updateCategory(id, request));
    }

    @DeleteMapping("/category/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/order")
    public ApiResponse<List<OrderVO>> orders() {
        return ApiResponse.success(adminService.listOrders());
    }

    @PutMapping("/order/{id}/status")
    public ApiResponse<Void> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        adminService.updateOrderStatus(id, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/user")
    public ApiResponse<List<UserInfoVO>> users() {
        return ApiResponse.success(adminService.listUsers());
    }

    @PutMapping("/user/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        adminService.updateUserStatus(id, request);
        return ApiResponse.success(null);
    }
}

