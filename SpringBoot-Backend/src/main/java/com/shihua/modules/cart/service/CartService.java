package com.shihua.modules.cart.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shihua.common.exception.BusinessException;
import com.shihua.modules.cart.dto.AddCartRequest;
import com.shihua.modules.cart.dto.UpdateCartRequest;
import com.shihua.modules.cart.entity.Cart;
import com.shihua.modules.cart.mapper.CartMapper;
import com.shihua.modules.cart.vo.CartItemVO;
import com.shihua.modules.flower.entity.Flower;
import com.shihua.modules.flower.mapper.FlowerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class CartService {

    private final CartMapper cartMapper;
    private final FlowerMapper flowerMapper;

    public CartService(CartMapper cartMapper, FlowerMapper flowerMapper) {
        this.cartMapper = cartMapper;
        this.flowerMapper = flowerMapper;
    }

    public List<CartItemVO> list(Long userId) {
        return cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getAddTime))
            .stream()
            .map(this::toCartItem)
            .toList();
    }

    @Transactional
    public List<CartItemVO> add(Long userId, AddCartRequest request) {
        Flower flower = requireAvailableFlower(request.getFlowerId());
        int quantity = request.getQuantity() == null ? 1 : request.getQuantity();
        if (quantity > flower.getStock()) {
            throw new BusinessException("Insufficient stock");
        }

        Cart existing = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
            .eq(Cart::getUserId, userId)
            .eq(Cart::getFlowerId, request.getFlowerId()));
        if (existing == null) {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setFlowerId(request.getFlowerId());
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        } else {
            int newQuantity = existing.getQuantity() + quantity;
            if (newQuantity > flower.getStock()) {
                throw new BusinessException("Insufficient stock");
            }
            existing.setQuantity(newQuantity);
            cartMapper.updateById(existing);
        }
        return list(userId);
    }

    @Transactional
    public List<CartItemVO> update(Long userId, Long cartId, UpdateCartRequest request) {
        Cart cart = requireUserCart(userId, cartId);
        Flower flower = requireAvailableFlower(cart.getFlowerId());
        if (request.getQuantity() > flower.getStock()) {
            throw new BusinessException("Insufficient stock");
        }
        cart.setQuantity(request.getQuantity());
        cartMapper.updateById(cart);
        return list(userId);
    }

    @Transactional
    public List<CartItemVO> delete(Long userId, Long cartId) {
        requireUserCart(userId, cartId);
        cartMapper.deleteById(cartId);
        return list(userId);
    }

    @Transactional
    public void clear(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
    }

    public Cart requireUserCart(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !Objects.equals(cart.getUserId(), userId)) {
            throw new BusinessException("Cart item not found");
        }
        return cart;
    }

    private Flower requireAvailableFlower(Long flowerId) {
        Flower flower = flowerMapper.selectById(flowerId);
        if (flower == null || !Objects.equals(flower.getStatus(), 1)) {
            throw new BusinessException("Flower not found or unavailable");
        }
        return flower;
    }

    private CartItemVO toCartItem(Cart cart) {
        Flower flower = flowerMapper.selectById(cart.getFlowerId());
        if (flower == null) {
            return new CartItemVO(cart.getCartId(), cart.getFlowerId(), "Deleted product", BigDecimal.ZERO, 0, "", cart.getQuantity(), BigDecimal.ZERO);
        }
        BigDecimal subtotal = flower.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
        return new CartItemVO(
            cart.getCartId(),
            cart.getFlowerId(),
            flower.getFlowerName(),
            flower.getPrice(),
            flower.getStock(),
            flower.getCoverImage(),
            cart.getQuantity(),
            subtotal
        );
    }
}

