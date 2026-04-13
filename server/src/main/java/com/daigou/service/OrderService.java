package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.common.PageResult;
import com.daigou.entity.OrderInfo;

public interface OrderService extends IService<OrderInfo> {
    PageResult<OrderInfo> getUserOrders(Long userId, Integer status, int page, int size);
    OrderInfo createOrder(Long userId, Long addressId, String remark);
}
