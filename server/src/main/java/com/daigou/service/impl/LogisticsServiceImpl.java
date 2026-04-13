package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.entity.Logistics;
import com.daigou.mapper.LogisticsMapper;
import com.daigou.service.LogisticsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogisticsServiceImpl extends ServiceImpl<LogisticsMapper, Logistics> implements LogisticsService {
    @Override
    public List<Logistics> getByOrderNo(String orderNo) {
        return list(new LambdaQueryWrapper<Logistics>()
                .eq(Logistics::getOrderNo, orderNo).orderByAsc(Logistics::getTime));
    }

    @Override
    public void addNode(Logistics logistics) {
        save(logistics);
    }
}
