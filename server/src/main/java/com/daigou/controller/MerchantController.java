package com.daigou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.daigou.common.PageResult;
import com.daigou.common.Result;
import com.daigou.entity.Merchant;
import com.daigou.entity.OrderInfo;
import com.daigou.entity.Product;
import com.daigou.service.MerchantService;
import com.daigou.service.OrderService;
import com.daigou.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/merchant")
public class MerchantController {
    @Autowired private MerchantService merchantService;
    @Autowired private ProductService productService;
    @Autowired private OrderService orderService;

    @GetMapping("/info")
    public Result<Merchant> info(@RequestAttribute Long userId) {
        return Result.ok(merchantService.getByUserId(userId));
    }

    @GetMapping("/products")
    public Result<PageResult<Product>> products(@RequestAttribute Long userId,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return Result.ok(productService.getPage(page, size, null, null, null));
    }

    @PostMapping("/product")
    public Result<?> saveProduct(@RequestAttribute Long userId, @RequestBody Product product) {
        product.setMerchantId(merchantService.getByUserId(userId).getId());
        productService.saveOrUpdate(product);
        return Result.ok("保存成功");
    }

    @PutMapping("/product/status/{id}")
    public Result<?> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Product p = productService.getById(id);
        p.setStatus(body.get("status"));
        productService.updateById(p);
        return Result.ok();
    }

    @GetMapping("/orders")
    public Result<PageResult<OrderInfo>> orders(@RequestAttribute Long userId,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        Merchant m = merchantService.getByUserId(userId);
        // 简化：获取所有订单
        return Result.ok(orderService.getUserOrders(null, null, page, size));
    }
}
