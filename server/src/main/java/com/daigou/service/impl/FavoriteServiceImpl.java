package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.entity.Favorite;
import com.daigou.mapper.FavoriteMapper;
import com.daigou.service.FavoriteService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
    @Override
    public void toggleFavorite(Long userId, Long productId) {
        Favorite exist = getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId));
        if (exist != null) removeById(exist.getId());
        else {
            Favorite f = new Favorite();
            f.setUserId(userId);
            f.setProductId(productId);
            save(f);
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        return count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId)) > 0;
    }
}
