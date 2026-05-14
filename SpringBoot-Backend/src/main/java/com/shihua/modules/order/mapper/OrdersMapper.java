package com.shihua.modules.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shihua.modules.order.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}

