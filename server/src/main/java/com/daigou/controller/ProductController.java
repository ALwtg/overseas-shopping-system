package com.daigou.controller;

import com.daigou.common.PageResult;
import com.daigou.common.Result;
import com.daigou.entity.Product;
import com.daigou.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired private ProductService productService;

    @GetMapping("/list")
    public Result<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return Result.ok(productService.getPage(page, size, categoryId, keyword, null));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productService.getById(id));
    }

    @GetMapping("/hot")
    public Result<?> hot() {
        return Result.ok(productService.getPage(1, 8, null, null, 1));
    }
}
