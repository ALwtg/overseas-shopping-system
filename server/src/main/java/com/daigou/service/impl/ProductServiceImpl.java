package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.common.PageResult;
import com.daigou.entity.Product;
import com.daigou.mapper.ProductMapper;
import com.daigou.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Override
    public PageResult<Product> getPage(Integer page, Integer size, Long categoryId, String keyword, Integer status) {
        Page<Product> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null && categoryId > 0) wrapper.eq(Product::getCategoryId, categoryId);
        if (StringUtils.isNotBlank(keyword)) wrapper.like(Product::getName, keyword);
        if (status != null) wrapper.eq(Product::getStatus, status);
        else wrapper.eq(Product::getStatus, 1).eq(Product::getDeleted, 0);
        wrapper.orderByDesc(Product::getIsHot, Product::getIsRecommend, Product::getSales, Product::getCreateTime);

        Page<Product> result = page(pageObj, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
