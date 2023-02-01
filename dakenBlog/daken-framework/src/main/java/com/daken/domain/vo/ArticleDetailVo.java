package com.daken.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailVo {
    //分类id
    private Long categoryId;
    //分类名称
    private String categoryName;
    //内容
    private String content;
    //创建时间
    private Date createTime;
    //文章id
    private Long id;
    //是否可以评论
    private String isComment;
    //标题
    private String title;
    //浏览量
    private Long viewCount;
}
