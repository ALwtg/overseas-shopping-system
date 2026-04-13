package com.daigou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daigou.entity.Category;
import com.daigou.mapper.CategoryMapper;
import com.daigou.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Override
    public List<Category> getTreeList() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1).orderByAsc(Category::getSortOrder);
        List<Category> all = list(wrapper);
        Map<Long, List<Category>> groupByParent = all.stream()
                .collect(Collectors.groupingBy(Category::getParentId));

        List<Category> tree = new ArrayList<>();
        for (Category cat : all) {
            if (cat.getParentId() == 0) {
                cat.setChildren(groupByParent.getOrDefault(cat.getId(), new ArrayList<>()));
                tree.add(cat);
            }
        }
        return tree;
    }
}
