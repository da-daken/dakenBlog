package com.daken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.domain.ResponseResult;
import com.daken.domain.dto.AddArticleDto;
import com.daken.domain.dto.ArticleDto;
import com.daken.domain.dto.ArticleUpdateDto;
import com.daken.domain.entity.Article;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto addArticleDto);

    ResponseResult selectArticlePage(Integer pageNum, Integer pageSize, ArticleDto articleDto);

    ResponseResult getInfo(Long id);

    ResponseResult edit(ArticleUpdateDto articleUpdateDto);
}
