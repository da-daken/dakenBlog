package com.daken.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.daken.domain.entity.ArticleTag;
import com.daken.mapper.ArticleTagMapper;
import com.daken.service.ArticleTagService;
import org.springframework.stereotype.Service;

/**
 * 文章标签关联表(ArticleTag)表服务实现类
 *
 * @author makejava
 * @since 2022-10-13 19:51:44
 */
@Service("articleTagService")
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

}

