package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.common.BusinessException;
import com.daigou.common.PageResult;
import com.daigou.entity.Review;
import com.daigou.mapper.ReviewMapper;
import com.daigou.service.OrderService;
import com.daigou.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
    @Autowired
    private OrderService orderService;

    @Override
    public PageResult<Review> getByProduct(Long productId, int page, int size) {
        Page<Review> p = new Page<>(page, size);
        LambdaQueryWrapper<Review> w = new LambdaQueryWrapper<>();
        w.eq(Review::getProductId, productId).eq(Review::getStatus, 1).orderByDesc(Review::getCreateTime);
        Page<Review> result = page(p, w);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public void submit(Review review) {
        long count = baseMapper.selectCount(new LambdaQueryWrapper<Review>().eq(Review::getOrderId, review.getOrderId()));
        if (count > 0) throw new BusinessException("该订单已评价");
        review.setStatus(1);
        save(review);
    }
}
