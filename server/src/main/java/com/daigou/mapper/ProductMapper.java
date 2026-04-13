package com.daigou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daigou.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
