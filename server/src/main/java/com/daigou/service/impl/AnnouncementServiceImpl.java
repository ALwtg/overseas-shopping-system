package com.daigou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.entity.Announcement;
import com.daigou.mapper.AnnouncementMapper;
import com.daigou.service.AnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
}
