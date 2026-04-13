package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {
    void toggleFavorite(Long userId, Long productId);
    boolean isFavorited(Long userId, Long productId);
}
