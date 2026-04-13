package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.entity.Merchant;

public interface MerchantService extends IService<Merchant> {
    Merchant getByUserId(Long userId);
}
