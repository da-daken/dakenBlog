package com.daken.runner;

import com.daken.domain.entity.Article;
import com.daken.service.ArticleService;
import com.daken.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ViewCountRunner implements CommandLineRunner {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void run(String... args) throws Exception {
        //查询博客信息（文章id ， 文章浏览量）
        List<Article> articles = articleService.list();
        Map<String , Integer> viewCountMap = articles.stream()
                .collect(Collectors.toMap(article1 -> article1.getId().toString(), article -> {
                    return article.getViewCount().intValue();
                }));
        //存入redis
        redisCache.setCacheMap("article:viewCount",viewCountMap);
    }
}
