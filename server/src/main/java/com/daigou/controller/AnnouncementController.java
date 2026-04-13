package com.daigou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.daigou.common.Result;
import com.daigou.entity.Announcement;
import com.daigou.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    @Autowired private AnnouncementService announcementService;

    @GetMapping("/list")
    public Result<List<Announcement>> list() {
        return Result.ok(announcementService.list(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, 1).orderByDesc(Announcement::getSortOrder, Announcement::getCreateTime)));
    }

    @GetMapping("/all")
    public Result<List<Announcement>> all() {
        return Result.ok(announcementService.list());
    }

    @PostMapping
    public Result<?> save(@RequestBody Announcement a) {
        announcementService.saveOrUpdate(a);
        return Result.ok("保存成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.ok("删除成功");
    }
}
