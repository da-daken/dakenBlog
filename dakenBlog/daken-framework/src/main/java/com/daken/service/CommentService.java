package com.daken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Comment;


/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2022-10-07 15:01:45
 */
public interface CommentService extends IService<Comment> {

    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    ResponseResult addComment(Comment comment);
}

