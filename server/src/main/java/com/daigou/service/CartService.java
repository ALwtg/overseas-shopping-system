package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.entity.Cart;
import java.util.List;

public interface CartService extends IService<Cart> {
    void addCart(Long userId, Long productId, Integer quantity);
    List<Cart> getUserCarts(Long userId);
    void updateQuantity(Long id, Long userId, Integer quantity);
    void removeItem(Long id, Long userId);
}
