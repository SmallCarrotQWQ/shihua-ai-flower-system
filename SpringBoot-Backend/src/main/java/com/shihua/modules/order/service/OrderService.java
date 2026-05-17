package com.shihua.modules.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shihua.common.exception.BusinessException;
import com.shihua.modules.cart.entity.Cart;
import com.shihua.modules.cart.mapper.CartMapper;
import com.shihua.modules.flower.entity.Flower;
import com.shihua.modules.flower.mapper.FlowerMapper;
import com.shihua.modules.order.dto.CreateOrderRequest;
import com.shihua.modules.order.entity.OrderItem;
import com.shihua.modules.order.entity.Orders;
import com.shihua.modules.order.mapper.OrderItemMapper;
import com.shihua.modules.order.mapper.OrdersMapper;
import com.shihua.modules.order.vo.OrderItemVO;
import com.shihua.modules.order.vo.OrderVO;
import com.shihua.modules.user.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    public static final int STATUS_PENDING_PAY = 0;
    public static final int STATUS_PENDING_DELIVER = 1;
    public static final int STATUS_PENDING_RECEIVE = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_CANCELED = 4;

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final FlowerMapper flowerMapper;
    private final AddressService addressService;

    public OrderService(OrdersMapper ordersMapper, OrderItemMapper orderItemMapper, CartMapper cartMapper, FlowerMapper flowerMapper, AddressService addressService) {
        this.ordersMapper = ordersMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.flowerMapper = flowerMapper;
        this.addressService = addressService;
    }

    @Transactional
    public OrderVO create(Long userId, CreateOrderRequest request) {
        List<Cart> carts = selectCarts(userId, request.getCartIds());
        if (carts.isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Flower flower = requireAvailableFlower(cart.getFlowerId());
            if (cart.getQuantity() > flower.getStock()) {
                throw new BusinessException(flower.getFlowerName() + " has insufficient stock");
            }
            totalAmount = totalAmount.add(flower.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        Orders order = new Orders();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(STATUS_PENDING_PAY);
        order.setAddressId(request.getAddressId());
        if (request.getAddressId() != null) {
            addressService.requireAddress(userId, request.getAddressId());
        }
        order.setRemark(request.getRemark());
        ordersMapper.insert(order);

        for (Cart cart : carts) {
            Flower flower = requireAvailableFlower(cart.getFlowerId());
            OrderItem item = new OrderItem();
            item.setOrderId(order.getOrderId());
            item.setFlowerId(flower.getFlowerId());
            item.setFlowerName(flower.getFlowerName());
            item.setPrice(flower.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(flower.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItemMapper.insert(item);

            boolean updated = flowerMapper.update(null, new LambdaUpdateWrapper<Flower>()
                .eq(Flower::getFlowerId, flower.getFlowerId())
                .ge(Flower::getStock, cart.getQuantity())
                .setSql("stock = stock - " + cart.getQuantity())
                .setSql("sales_count = sales_count + " + cart.getQuantity())) > 0;
            if (!updated) {
                throw new BusinessException(flower.getFlowerName() + " has insufficient stock");
            }
            cartMapper.deleteById(cart.getCartId());
        }

        return detail(userId, order.getOrderId());
    }

    public List<OrderVO> list(Long userId) {
        return ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime))
            .stream()
            .map(order -> toOrderVO(order, selectItems(order.getOrderId())))
            .toList();
    }

    public OrderVO detail(Long userId, Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null || !Objects.equals(order.getUserId(), userId)) {
            throw new BusinessException("Order not found");
        }
        return toOrderVO(order, selectItems(orderId));
    }

    @Transactional
    public OrderVO pay(Long userId, Long orderId) {
        Orders order = requireUserOrder(userId, orderId);
        if (!Objects.equals(order.getStatus(), STATUS_PENDING_PAY)) {
            throw new BusinessException("Only pending payment orders can be paid");
        }
        order.setStatus(STATUS_PENDING_DELIVER);
        order.setPayTime(LocalDateTime.now());
        ordersMapper.updateById(order);
        return detail(userId, orderId);
    }

    @Transactional
    public OrderVO confirmReceive(Long userId, Long orderId) {
        Orders order = requireUserOrder(userId, orderId);
        if (!Objects.equals(order.getStatus(), STATUS_PENDING_RECEIVE)) {
            throw new BusinessException("Only delivered orders can be confirmed");
        }
        order.setStatus(STATUS_COMPLETED);
        ordersMapper.updateById(order);
        return detail(userId, orderId);
    }

    @Transactional
    public OrderVO cancel(Long userId, Long orderId) {
        Orders order = requireUserOrder(userId, orderId);
        if (!Objects.equals(order.getStatus(), STATUS_PENDING_PAY) && !Objects.equals(order.getStatus(), STATUS_PENDING_DELIVER)) {
            throw new BusinessException("Current order cannot be canceled");
        }
        restoreStock(orderId);
        order.setStatus(STATUS_CANCELED);
        ordersMapper.updateById(order);
        return detail(userId, orderId);
    }

    @Transactional
    public void adminDeliver(Long orderId) {
        Orders order = requireOrder(orderId);
        if (!Objects.equals(order.getStatus(), STATUS_PENDING_DELIVER)) {
            throw new BusinessException("Only paid orders can be delivered");
        }
        order.setStatus(STATUS_PENDING_RECEIVE);
        order.setDeliverTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    @Transactional
    public void adminCancel(Long orderId) {
        Orders order = requireOrder(orderId);
        if (!Objects.equals(order.getStatus(), STATUS_PENDING_PAY) && !Objects.equals(order.getStatus(), STATUS_PENDING_DELIVER)) {
            throw new BusinessException("Current order cannot be canceled");
        }
        restoreStock(orderId);
        order.setStatus(STATUS_CANCELED);
        ordersMapper.updateById(order);
    }

    private List<Cart> selectCarts(Long userId, List<Long> cartIds) {
        LambdaQueryWrapper<Cart> query = new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId);
        if (cartIds != null && !cartIds.isEmpty()) {
            query.in(Cart::getCartId, cartIds);
        }
        return cartMapper.selectList(query);
    }

    private Flower requireAvailableFlower(Long flowerId) {
        Flower flower = flowerMapper.selectById(flowerId);
        if (flower == null || !Objects.equals(flower.getStatus(), 1)) {
            throw new BusinessException("Flower not found or unavailable");
        }
        return flower;
    }

    private List<OrderItem> selectItems(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
    }

    private Orders requireUserOrder(Long userId, Long orderId) {
        Orders order = requireOrder(orderId);
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new BusinessException("Order not found");
        }
        return order;
    }

    private Orders requireOrder(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found");
        }
        return order;
    }

    private void restoreStock(Long orderId) {
        for (OrderItem item : selectItems(orderId)) {
            flowerMapper.update(null, new LambdaUpdateWrapper<Flower>()
                .eq(Flower::getFlowerId, item.getFlowerId())
                .setSql("stock = stock + " + item.getQuantity())
                .setSql("sales_count = GREATEST(sales_count - " + item.getQuantity() + ", 0)"));
        }
    }

    private OrderVO toOrderVO(Orders order, List<OrderItem> items) {
        return new OrderVO(
            order.getOrderId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getAddressId(),
            order.getRemark(),
            order.getCreateTime(),
            items.stream()
                .map(item -> new OrderItemVO(item.getItemId(), item.getFlowerId(), item.getFlowerName(), item.getPrice(), item.getQuantity(), item.getSubtotal()))
                .toList()
        );
    }

}
