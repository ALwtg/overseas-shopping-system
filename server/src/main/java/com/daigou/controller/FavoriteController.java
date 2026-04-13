package com.daigou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.daigou.common.PageResult;
import com.daigou.common.Result;
import com.daigou.entity.Favorite;
import com.daigou.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    @Autowired private FavoriteService favoriteService;

    @PostMapping("/toggle/{productId}")
    public Result<?> toggle(@RequestAttribute Long userId, @PathVariable Long productId) {
        favoriteService.toggleFavorite(userId, productId);
        return Result.ok();
    }

    @GetMapping("/check/{productId}")
    public Result<Boolean> check(@RequestAttribute Long userId, @PathVariable Long productId) {
        return Result.ok(favoriteService.isFavorited(userId, productId));
    }

    @GetMapping("/list")
    public Result<?> list(@RequestAttribute Long userId) {
        return Result.ok(favoriteService.list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime)));
    }
}
