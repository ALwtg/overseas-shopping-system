package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.entity.Merchant;
import com.daigou.mapper.MerchantMapper;
import com.daigou.service.MerchantService;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {
    @Override
    public Merchant getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getUserId, userId));
    }
}
