package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.common.BusinessException;
import com.daigou.common.PageResult;
import com.daigou.entity.*;
import com.daigou.mapper.AddressMapper;
import com.daigou.mapper.CartMapper;
import com.daigou.mapper.OrderInfoMapper;
import com.daigou.mapper.OrderItemMapper;
import com.daigou.mapper.ProductMapper;
import com.daigou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public PageResult<OrderInfo> getUserOrders(Long userId, Integer status, int page, int size) {
        Page<OrderInfo> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId);
        if (status != null) wrapper.eq(OrderInfo::getStatus, status);
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        Page<OrderInfo> result = page(pageObj, wrapper);
        result.getRecords().forEach(this::fillStatusText);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo createOrder(Long userId, Long addressId, String remark) {
        List<Cart> cartItems = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId).eq(Cart::getSelected, 1));
        if (cartItems.isEmpty()) throw new BusinessException("请先选择商品");

        Address address = addressMapper.selectById(addressId);
        if (address == null) throw new BusinessException("收货地址不存在");

        // 加载商品信息
        List<Product> products = productMapper.selectBatchIds(
                cartItems.stream().map(Cart::getProductId).collect(Collectors.toList()));

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart item : cartItems) {
            Product product = products.stream().filter(p -> p.getId().equals(item.getProductId())).findFirst().orElse(null);
            if (product == null) continue;
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Product firstProduct = products.stream().findFirst().orElse(null);
        OrderInfo order = new OrderInfo();
        order.setOrderNo("DG" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        order.setUserId(userId);
        order.setMerchantId(firstProduct != null ? firstProduct.getMerchantId() : null);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setFreight(BigDecimal.ZERO);
        order.setStatus(0); // 待付款
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + " " + address.getCity() + " " + address.getDistrict() + " " + address.getDetailAddress());
        order.setRemark(remark);
        save(order);

        for (Cart c : cartItems) {
            Product product = products.stream().filter(p -> p.getId().equals(c.getProductId())).findFirst().orElse(null);
            if (product == null) continue;
            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId());
            oi.setProductId(c.getProductId());
            oi.setProductName(product.getName());
            oi.setProductImage(product.getCoverImage());
            oi.setPrice(product.getPrice());
            oi.setQuantity(c.getQuantity());
            oi.setSubTotal(product.getPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
            orderItemMapper.insert(oi);
        }

        // 清空已选购物车项
        List<Long> cartIds = cartItems.stream().map(Cart::getId).collect(Collectors.toList());
        cartIds.forEach(id -> cartMapper.deleteById(id));

        return order;
    }

    private void fillStatusText(OrderInfo order) {
        String[] texts = {"待付款", "待采购", "运输中", "清关中", "派送中", "已完成", "已取消", "退款中"};
        if (order.getStatus() >= 0 && order.getStatus() < texts.length) {
            order.setStatusText(texts[order.getStatus()]);
        }
    }
}
