package com.daigou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daigou.common.PageResult;
import com.daigou.common.Result;
import com.daigou.entity.*;
import com.daigou.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired private UserMapper userMapper;

    // 用户管理
    @GetMapping("/users")
    public Result<PageResult<User>> users(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String keyword) {
        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> w = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) w.like(User::getUsername, keyword).or().like(User::getNickname, keyword);
        w.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(p, w);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }

    @PutMapping("/user/status/{id}")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        User u = userMapper.selectById(id);
        if (u != null) { u.setStatus(body.get("status")); userMapper.updateById(u); }
        return Result.ok();
    }

    // 商家审核
    @GetMapping("/merchants")
    public Result<List<Merchant>> merchants() {
        return Result.ok(null); // 简化实现
    }

    @PutMapping("/merchant/status/{id}")
    public Result<?> updateMerchantStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return Result.ok();
    }

    // 商品审核
    @GetMapping("/products/pending")
    public Result<?> pendingProducts() {
        return Result.ok(null);
    }

    // 统计
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("todayOrders", 0);
        data.put("todaySales", "0.00");
        data.put("totalProducts", 0);
        return Result.ok(data);
    }
}
