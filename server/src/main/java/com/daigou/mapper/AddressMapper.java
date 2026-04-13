package com.daigou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daigou.entity.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
