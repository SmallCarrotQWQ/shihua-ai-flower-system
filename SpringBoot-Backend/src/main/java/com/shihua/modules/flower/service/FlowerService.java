package com.shihua.modules.flower.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shihua.common.exception.BusinessException;
import com.shihua.modules.flower.entity.Flower;
import com.shihua.modules.flower.entity.FlowerCategory;
import com.shihua.modules.flower.mapper.FlowerCategoryMapper;
import com.shihua.modules.flower.mapper.FlowerMapper;
import com.shihua.modules.flower.vo.CategoryVO;
import com.shihua.modules.flower.vo.FlowerVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FlowerService {

    private final FlowerMapper flowerMapper;
    private final FlowerCategoryMapper categoryMapper;

    public FlowerService(FlowerMapper flowerMapper, FlowerCategoryMapper categoryMapper) {
        this.flowerMapper = flowerMapper;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryVO> listCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<FlowerCategory>()
                .orderByAsc(FlowerCategory::getSortOrder)
                .orderByAsc(FlowerCategory::getCategoryId))
            .stream()
            .map(category -> new CategoryVO(category.getCategoryId(), category.getCategoryName(), category.getParentId(), category.getSortOrder()))
            .toList();
    }

    public List<FlowerVO> listFlowers(Long categoryId, String keyword) {
        LambdaQueryWrapper<Flower> query = new LambdaQueryWrapper<Flower>()
            .eq(Flower::getStatus, 1)
            .orderByDesc(Flower::getSalesCount)
            .orderByDesc(Flower::getCreateTime);
        if (categoryId != null) {
            query.eq(Flower::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            query.and(wrapper -> wrapper
                .like(Flower::getFlowerName, keyword.trim())
                .or()
                .like(Flower::getFlowerLanguage, keyword.trim()));
        }
        return toFlowerVOList(flowerMapper.selectList(query));
    }

    public List<FlowerVO> hotFlowers(Integer limit) {
        int safeLimit = limit == null || limit <= 0 ? 10 : Math.min(limit, 20);
        return toFlowerVOList(flowerMapper.selectList(new LambdaQueryWrapper<Flower>()
            .eq(Flower::getStatus, 1)
            .orderByDesc(Flower::getSalesCount)
            .last("LIMIT " + safeLimit)));
    }

    public FlowerVO detail(Long flowerId) {
        Flower flower = flowerMapper.selectById(flowerId);
        if (flower == null || !Objects.equals(flower.getStatus(), 1)) {
            throw new BusinessException("Flower not found or unavailable");
        }
        return toFlowerVOList(List.of(flower)).get(0);
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
                flower.getSalesCount()
            );
        }).toList();
    }
}

