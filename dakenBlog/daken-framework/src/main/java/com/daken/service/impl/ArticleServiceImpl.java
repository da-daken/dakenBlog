package com.daken.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daken.constants.SystemConstants;
import com.daken.domain.ResponseResult;
import com.daken.domain.dto.AddArticleDto;
import com.daken.domain.dto.ArticleDto;
import com.daken.domain.dto.ArticleUpdateDto;
import com.daken.domain.entity.Article;
import com.daken.domain.entity.ArticleTag;
import com.daken.domain.entity.Category;
import com.daken.domain.vo.*;
import com.daken.mapper.ArticleMapper;
import com.daken.service.ArticleService;
import com.daken.service.ArticleTagService;
import com.daken.service.CategoryService;
import com.daken.service.UserService;
import com.daken.utils.BeanCopyUtils;
import com.daken.utils.RedisCache;

import com.daken.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private UserService userService;

    @Override
    public ResponseResult hotArticleList() {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        //查询条件：
        //1 必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //2 对浏览量进行排序(降序) asc升序 desc降序
        queryWrapper.orderByDesc(Article::getViewCount);
        //3 最多查询10条
        Page<Article> page=new Page<>(SystemConstants.PAGE_CURRENT,SystemConstants.PAGE_SIZE);
        page(page,queryWrapper);
        List<Article> articles = page.getRecords();
        articles=articles.stream()
                .map(article -> {
                    //从redis中获取viewCount
                    Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article.getId().toString());
                    article.setViewCount(viewCount.longValue());
                    return article;
                })
                .collect(Collectors.toList());

        //bean拷贝
        List<HotArticleVo> hotArticleVos = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);
        return ResponseResult.okResult(hotArticleVos);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果 有categoryId 就要 查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0 ,Article::getCategoryId,categoryId);
        // 状态是正式发布的
        lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        // 对isTop进行降序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);

        List<Article> articles = page.getRecords();
        //查询categoryName
        articles = articles.stream()
                .map(article -> {
                    Article article1 = article.setCategoryName(categoryService.getById(article.getCategoryId()).getName());
                    //从redis中获取viewCount
                    Integer viewCount = redisCache.getCacheMapValue("article:viewCount", article1.getId().toString());
                    article1.setViewCount(viewCount.longValue());
                    return article1;
                })
                .collect(Collectors.toList());
        //将文章的userName加进去
        articles = articles.stream()
                .map(article -> article.setUserName(userService.getById(article.getUserId()).getUserName()))
                .collect(Collectors.toList());

//        for (Article article : articles) {
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(articles, ArticleListVo.class);
        PageVo pageVo=new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        //从redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成VO
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //根据分类id查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中对应的id浏览量
        redisCache.incrementCacheMapValue("article:viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto addArticleDto) {
        //添加文章到文章表里
        Article article = BeanCopyUtils.copyBean(addArticleDto, Article.class);
        save(article);
        //将文章和标签的关系添加到中间表中
        List<ArticleTag> articleTags = addArticleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult selectArticlePage(Integer pageNum, Integer pageSize, ArticleDto articleDto) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        //对用户进行权限判断
        //要是管理员全部放行
        if(SecurityUtils.getUserId() != 1){
            queryWrapper.eq(Article::getUserId,SecurityUtils.getUserId());
        }
        //要求能根据标题和摘要 模糊查询
        queryWrapper.like(StringUtils.hasText(articleDto.getTitle()),Article::getTitle,articleDto.getTitle());
        queryWrapper.like(StringUtils.hasText(articleDto.getSummary()),Article::getSummary,articleDto.getSummary());
        //分页
        Page<Article> page=new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);
        //封装
        List<Article> records = page.getRecords();
        //将文章userName传进去
        records = records.stream()
                .map(article -> article.setUserName(userService.getById(article.getUserId()).getUserName()))
                .collect(Collectors.toList());
        List<ArticleBackVo> articleBackVos = BeanCopyUtils.copyBeanList(records, ArticleBackVo.class);
        PageVo pageVo = new PageVo(articleBackVos, page.getTotal());
        return  ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getInfo(Long id) {
        //根据id查Article
        Article article = getById(id);
        //获取相关标签
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId,article.getId());
        List<ArticleTag> articleTagList = articleTagService.list(queryWrapper);
        List<Long> tags = articleTagList.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toList());
        ArticleVo articleVo = BeanCopyUtils.copyBean(article, ArticleVo.class);
        articleVo.setTags(tags);
        return ResponseResult.okResult(articleVo);
    }

    @Override
    public ResponseResult edit(ArticleUpdateDto articleUpdateDto) {
        //将dto转换成Article
        Article article = BeanCopyUtils.copyBean(articleUpdateDto, Article.class);
        article.setStatus("2");
        updateById(article);
        //将dto中的tag分离出
        //删除原有的标签和文章的关联
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId,article.getId());
        articleTagService.remove(queryWrapper);
        //添加新的关联信息
        List<ArticleTag> articleTags = articleUpdateDto.getTags().stream()
                .map(aLong -> new ArticleTag(article.getId(), aLong))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }
}
