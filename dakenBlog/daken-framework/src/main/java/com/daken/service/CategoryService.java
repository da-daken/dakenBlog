package com.daken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Category;

/**
 * 分类表(SgCategory)表服务接口
 *
 * @author makejava
 * @since 2022-09-24 10:55:48
 */
public interface CategoryService extends IService<Category> {
    ResponseResult getCategoryList();

    ResponseResult listAllCategory();

    ResponseResult selectCategoryPage(Category category, Integer pageNum, Integer pageSize);
}

