package com.shihua.modules.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shihua.common.exception.BusinessException;
import com.shihua.modules.admin.dto.AdminCategoryRequest;
import com.shihua.modules.admin.dto.AdminFlowerRequest;
import com.shihua.modules.admin.dto.StatusRequest;
import com.shihua.modules.flower.entity.Flower;
import com.shihua.modules.flower.entity.FlowerCategory;
import com.shihua.modules.flower.mapper.FlowerCategoryMapper;
import com.shihua.modules.flower.mapper.FlowerMapper;
import com.shihua.modules.flower.service.FlowerService;
import com.shihua.modules.flower.vo.CategoryVO;
import com.shihua.modules.flower.vo.FlowerVO;
import com.shihua.modules.order.entity.Orders;
import com.shihua.modules.order.mapper.OrdersMapper;
import com.shihua.modules.order.service.OrderService;
import com.shihua.modules.order.vo.OrderVO;
import com.shihua.modules.user.entity.SysUser;
import com.shihua.modules.user.mapper.SysUserMapper;
import com.shihua.modules.user.vo.UserInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final FlowerMapper flowerMapper;
    private final FlowerCategoryMapper categoryMapper;
    private final OrdersMapper ordersMapper;
    private final SysUserMapper userMapper;
    private final FlowerService flowerService;
    private final OrderService orderService;

    public AdminService(
        FlowerMapper flowerMapper,
        FlowerCategoryMapper categoryMapper,
        OrdersMapper ordersMapper,
        SysUserMapper userMapper,
        FlowerService flowerService,
        OrderService orderService
    ) {
        this.flowerMapper = flowerMapper;
        this.categoryMapper = categoryMapper;
        this.ordersMapper = ordersMapper;
        this.userMapper = userMapper;
        this.flowerService = flowerService;
        this.orderService = orderService;
    }

    public Map<String, Object> dashboardStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<Orders> todayOrders = ordersMapper.selectList(new LambdaQueryWrapper<Orders>().ge(Orders::getCreateTime, todayStart));
        BigDecimal salesAmount = ordersMapper.selectList(null).stream()
            .map(Orders::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long userCount = userMapper.selectCount(null);
        long flowerCount = flowerMapper.selectCount(null);
        return Map.of(
            "ordersToday", todayOrders.size(),
            "salesAmount", salesAmount,
            "userCount", userCount,
            "flowerCount", flowerCount
        );
    }

    public List<FlowerVO> listFlowers() {
        return toFlowerVOList(flowerMapper.selectList(new LambdaQueryWrapper<Flower>().orderByDesc(Flower::getCreateTime)));
    }

    @Transactional
    public FlowerVO createFlower(AdminFlowerRequest request) {
        ensureCategoryExists(request.getCategoryId());
        Flower flower = new Flower();
        applyFlowerRequest(flower, request);
        flower.setSalesCount(0);
        flowerMapper.insert(flower);
        return toFlowerVOList(List.of(flower)).get(0);
    }

    @Transactional
    public FlowerVO updateFlower(Long id, AdminFlowerRequest request) {
        ensureCategoryExists(request.getCategoryId());
        Flower flower = requireFlower(id);
        applyFlowerRequest(flower, request);
        flowerMapper.updateById(flower);
        return toFlowerVOList(List.of(flower)).get(0);
    }

    @Transactional
    public void updateFlowerStatus(Long id, StatusRequest request) {
        Flower flower = requireFlower(id);
        flower.setStatus(request.getStatus());
        flowerMapper.updateById(flower);
    }

    @Transactional
    public void deleteFlower(Long id) {
        requireFlower(id);
        flowerMapper.deleteById(id);
    }

    public List<CategoryVO> listCategories() {
        return flowerService.listCategories();
    }

    @Transactional
    public CategoryVO createCategory(AdminCategoryRequest request) {
        FlowerCategory category = new FlowerCategory();
        category.setCategoryName(request.getCategoryName());
        category.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        categoryMapper.insert(category);
        return new CategoryVO(category.getCategoryId(), category.getCategoryName(), category.getParentId(), category.getSortOrder());
    }

    @Transactional
    public CategoryVO updateCategory(Long id, AdminCategoryRequest request) {
        FlowerCategory category = requireCategory(id);
        category.setCategoryName(request.getCategoryName());
        category.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        categoryMapper.updateById(category);
        return new CategoryVO(category.getCategoryId(), category.getCategoryName(), category.getParentId(), category.getSortOrder());
    }

    @Transactional
    public void deleteCategory(Long id) {
        long flowerCount = flowerMapper.selectCount(new LambdaQueryWrapper<Flower>().eq(Flower::getCategoryId, id));
        if (flowerCount > 0) {
            throw new BusinessException("Category has flowers");
        }
        categoryMapper.deleteById(id);
    }

    public List<OrderVO> listOrders() {
        return ordersMapper.selectList(new LambdaQueryWrapper<Orders>().orderByDesc(Orders::getCreateTime))
            .stream()
            .map(order -> orderService.detail(order.getUserId(), order.getOrderId()))
            .toList();
    }

    @Transactional
    public void updateOrderStatus(Long id, StatusRequest request) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        order.setStatus(request.getStatus());
        if (request.getStatus() != null && request.getStatus() == 2) {
            order.setDeliverTime(LocalDateTime.now());
        }
        ordersMapper.updateById(order);
    }

    public List<UserInfoVO> listUsers() {
        return userMapper.selectList(new LambdaQueryWrapper<SysUser>().orderByDesc(SysUser::getCreateTime))
            .stream()
            .map(this::toUserInfo)
            .toList();
    }

    @Transactional
    public void updateUserStatus(Long id, StatusRequest request) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        user.setStatus(request.getStatus());
        userMapper.updateById(user);
    }

    private void applyFlowerRequest(Flower flower, AdminFlowerRequest request) {
        flower.setFlowerName(request.getFlowerName());
        flower.setCategoryId(request.getCategoryId());
        flower.setPrice(request.getPrice());
        flower.setStock(request.getStock() == null ? 0 : request.getStock());
        flower.setCoverImage(request.getCoverImage());
        flower.setDescription(request.getDescription());
        flower.setFlowerLanguage(request.getFlowerLanguage());
        flower.setCareGuide(request.getCareGuide());
        flower.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }

    private Flower requireFlower(Long id) {
        Flower flower = flowerMapper.selectById(id);
        if (flower == null) {
            throw new BusinessException("Flower not found");
        }
        return flower;
    }

    private FlowerCategory requireCategory(Long id) {
        FlowerCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("Category not found");
        }
        return category;
    }

    private void ensureCategoryExists(Long id) {
        requireCategory(id);
    }

    private UserInfoVO toUserInfo(SysUser user) {
        return new UserInfoVO(user.getUserId(), user.getUsername(), user.getGender(), user.getPhone(), user.getEmail(), user.getAvatar(), user.getRole(), user.getStatus());
    }

    private List<FlowerVO> toFlowerVOList(List<Flower> flowers) {
        Map<Long, FlowerCategory> categoryMap = categoryMapper.selectList(null)
            .stream()
            .collect(Collectors.toMap(FlowerCategory::getCategoryId, Function.identity()));
        return flowers.stream().map(flower -> {
            FlowerCategory category = categoryMap.get(flower.getCategoryId());
            return new FlowerVO(
                flower.getFlowerId(),
                flower.getFlowerName(),
                flower.getCategoryId(),
                category == null ? "" : category.getCategoryName(),
                flower.getPrice(),
                flower.getStock(),
                flower.getCoverImage(),
                flower.getDescription(),
                flower.getFlowerLanguage(),
                flower.getCareGuide(),
                flower.getSalesCount(),
                flower.getStatus()
            );
        }).toList();
    }
}
