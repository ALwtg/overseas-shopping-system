package com.daigou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daigou.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getTreeList();
}
