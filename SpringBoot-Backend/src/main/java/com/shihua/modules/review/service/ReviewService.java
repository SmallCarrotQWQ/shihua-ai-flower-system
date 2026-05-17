package com.shihua.modules.review.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shihua.common.exception.BusinessException;
import com.shihua.modules.flower.entity.Flower;
import com.shihua.modules.flower.mapper.FlowerMapper;
import com.shihua.modules.order.entity.OrderItem;
import com.shihua.modules.order.entity.Orders;
import com.shihua.modules.order.mapper.OrderItemMapper;
import com.shihua.modules.order.mapper.OrdersMapper;
import com.shihua.modules.review.dto.ReviewRequest;
import com.shihua.modules.review.entity.Review;
import com.shihua.modules.review.mapper.ReviewMapper;
import com.shihua.modules.review.vo.ReviewVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ReviewService {
    private final ReviewMapper reviewMapper;
    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final FlowerMapper flowerMapper;

    public ReviewService(ReviewMapper reviewMapper, OrdersMapper ordersMapper, OrderItemMapper orderItemMapper, FlowerMapper flowerMapper) {
        this.reviewMapper = reviewMapper;
        this.ordersMapper = ordersMapper;
        this.orderItemMapper = orderItemMapper;
        this.flowerMapper = flowerMapper;
    }

    @Transactional
    public ReviewVO create(Long userId, Long orderId, ReviewRequest request) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null || !Objects.equals(order.getUserId(), userId)) {
            throw new BusinessException("Order not found");
        }
        if (!Objects.equals(order.getStatus(), 3)) {
            throw new BusinessException("Only completed orders can be reviewed");
        }
        boolean containsFlower = orderItemMapper.selectCount(new LambdaQueryWrapper<OrderItem>()
            .eq(OrderItem::getOrderId, orderId)
            .eq(OrderItem::getFlowerId, request.getFlowerId())) > 0;
        if (!containsFlower) {
            throw new BusinessException("Flower not found in order");
        }
        boolean exists = reviewMapper.selectCount(new LambdaQueryWrapper<Review>()
            .eq(Review::getUserId, userId)
            .eq(Review::getOrderId, orderId)
            .eq(Review::getFlowerId, request.getFlowerId())) > 0;
        if (exists) {
            throw new BusinessException("Review already exists");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setOrderId(orderId);
        review.setFlowerId(request.getFlowerId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        applySimpleSentiment(review);
        reviewMapper.insert(review);
        return toVO(review);
    }

    public List<ReviewVO> listByFlower(Long flowerId) {
        return reviewMapper.selectList(new LambdaQueryWrapper<Review>()
                .eq(Review::getFlowerId, flowerId)
                .orderByDesc(Review::getCreateTime))
            .stream()
            .map(this::toVO)
            .toList();
    }

    public List<ReviewVO> listAll() {
        return reviewMapper.selectList(new LambdaQueryWrapper<Review>().orderByDesc(Review::getCreateTime))
            .stream()
            .map(this::toVO)
            .toList();
    }

    private void applySimpleSentiment(Review review) {
        Flower flower = flowerMapper.selectById(review.getFlowerId());
        String text = (review.getContent() == null ? "" : review.getContent()) + (flower == null ? "" : flower.getFlowerName());
        if (review.getRating() >= 4 || text.contains("好") || text.contains("新鲜") || text.contains("喜欢")) {
            review.setSentiment("positive");
        } else if (review.getRating() <= 2 || text.contains("差") || text.contains("慢") || text.contains("枯")) {
            review.setSentiment("negative");
        } else {
            review.setSentiment("neutral");
        }
        review.setKeywords("");
    }

    private ReviewVO toVO(Review review) {
        return new ReviewVO(review.getReviewId(), review.getUserId(), review.getFlowerId(), review.getOrderId(), review.getRating(), review.getContent(), review.getSentiment(), review.getKeywords(), review.getCreateTime());
    }
}
