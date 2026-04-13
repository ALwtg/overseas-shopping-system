package com.daigou.controller;

import com.daigou.common.Result;
import com.daigou.entity.Logistics;
import com.daigou.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logistics")
public class LogisticsController {
    @Autowired private LogisticsService logisticsService;

    @GetMapping("/order/{orderNo}")
    public Result<List<Logistics>> list(@PathVariable String orderNo) {
        return Result.ok(logisticsService.getByOrderNo(orderNo));
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Logistics logistics) {
        logisticsService.addNode(logistics);
        return Result.ok("更新成功");
    }
}
