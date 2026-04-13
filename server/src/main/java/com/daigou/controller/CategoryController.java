package com.daigou.controller;

import com.daigou.common.Result;
import com.daigou.entity.Category;
import com.daigou.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired private CategoryService categoryService;

    @GetMapping("/tree")
    public Result<List<Category>> tree() {
        return Result.ok(categoryService.getTreeList());
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryService.list());
    }
}
