package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.common.PageResult;
import com.daigou.entity.Review;

public interface ReviewService extends IService<Review> {
    PageResult<Review> getByProduct(Long productId, int page, int size);
    void submit(Review review);
}
