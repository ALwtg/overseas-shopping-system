package com.daigou.controller;

import com.daigou.common.PageResult;
import com.daigou.common.Result;
import com.daigou.entity.OrderInfo;
import com.daigou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired private OrderService orderService;

    @GetMapping("/list")
    public Result<PageResult<OrderInfo>> list(@RequestAttribute Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.ok(orderService.getUserOrders(userId, status, page, size));
    }

    @PostMapping("/create")
    public Result<OrderInfo> create(@RequestAttribute Long userId, @RequestBody Map<String, String> body) {
        return Result.ok(orderService.createOrder(userId,
                Long.valueOf(body.getOrDefault("addressId", "0")),
                body.get("remark")));
    }

    @GetMapping("/{id}")
    public Result<OrderInfo> detail(@PathVariable Long id) {
        return Result.ok(orderService.getById(id));
    }
}
