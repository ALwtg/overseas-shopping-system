package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.common.PageResult;
import com.daigou.entity.Product;

public interface ProductService extends IService<Product> {
    PageResult<Product> getPage(Integer page, Integer size, Long categoryId, String keyword, Integer status);
}
