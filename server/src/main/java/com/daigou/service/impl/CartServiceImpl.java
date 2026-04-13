package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.common.BusinessException;
import com.daigou.entity.Cart;
import com.daigou.entity.Product;
import com.daigou.mapper.CartMapper;
import com.daigou.mapper.ProductMapper;
import com.daigou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public void addCart(Long userId, Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1 || product.getDeleted() != 0)
            throw new BusinessException("商品不存在或已下架");

        Cart exist = getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId).eq(Cart::getProductId, productId));
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            updateById(exist);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setSelected(1);
            save(cart);
        }
    }

    @Override
    public List<Cart> getUserCarts(Long userId) {
        return list(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId).orderByDesc(Cart::getCreateTime));
    }

    @Override
    public void updateQuantity(Long id, Long userId, Integer quantity) {
        if (quantity < 1) throw new BusinessException("数量不能小于1");
        Cart cart = getOne(new LambdaQueryWrapper<Cart>().eq(Cart::getId, id).eq(Cart::getUserId, userId));
        if (cart == null) throw new BusinessException("购物车项不存在");
        cart.setQuantity(quantity);
        updateById(cart);
    }

    @Override
    public void removeItem(Long id, Long userId) {
        remove(new LambdaQueryWrapper<Cart>().eq(Cart::getId, id).eq(Cart::getUserId, userId));
    }
}
