package com.daigou.controller;

import com.daigou.common.Result;
import com.daigou.entity.Cart;
import com.daigou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired private CartService cartService;

    @GetMapping("/list")
    public Result<List<Cart>> list(@RequestAttribute Long userId) {
        return Result.ok(cartService.getUserCarts(userId));
    }

    @PostMapping("/add")
    public Result<?> add(@RequestAttribute Long userId, @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = Integer.valueOf(body.getOrDefault("quantity", 1).toString());
        cartService.addCart(userId, productId, quantity);
        return Result.ok("已加入购物车");
    }

    @PutMapping("/{id}")
    public Result<?> updateQty(@RequestAttribute Long userId, @PathVariable Long id,
                               @RequestBody Map<String, Object> body) {
        cartService.updateQuantity(id, userId, Integer.valueOf(body.get("quantity").toString()));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> remove(@RequestAttribute Long userId, @PathVariable Long id) {
        cartService.removeItem(id, userId);
        return Result.ok("已移除");
    }
}
