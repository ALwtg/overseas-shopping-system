package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.entity.Logistics;
import java.util.List;

public interface LogisticsService extends IService<Logistics> {
    List<Logistics> getByOrderNo(String orderNo);
    void addNode(Logistics logistics);
}
