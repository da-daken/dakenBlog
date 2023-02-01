package com.daken.controller;

import com.daken.domain.ResponseResult;
import com.daken.domain.dto.AddArticleDto;
import com.daken.domain.dto.ArticleDto;
import com.daken.domain.dto.ArticleUpdateDto;
import com.daken.domain.entity.Article;
import com.daken.service.ArticleService;
import com.daken.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto addArticleDto){
        addArticleDto.setUserId(SecurityUtils.getUserId());
        return articleService.add(addArticleDto);
    }

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, ArticleDto articleDto){
        return articleService.selectArticlePage(pageNum,pageSize,articleDto);
    }

    @GetMapping("/{id}")
    public ResponseResult getInfo(@PathVariable Long id){
        return articleService.getInfo(id);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody ArticleUpdateDto articleUpdateDto){
        return articleService.edit(articleUpdateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id){
        articleService.removeById(id);
        return ResponseResult.okResult();
    }

    @PreAuthorize("@ps.hasPermission('content:article:judge')")
    @PutMapping("/changeArticleStatus")
    public ResponseResult changeArticleStatus(@RequestBody Article article){
        articleService.updateById(article);
        return ResponseResult.okResult();
    }
}
