package com.daken.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daken.constants.SystemConstants;
import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Comment;
import com.daken.domain.vo.CommentVo;
import com.daken.domain.vo.PageVo;
import com.daken.enums.AppHttpCodeEnum;
import com.daken.exception.SystemException;
import com.daken.mapper.CommentMapper;
import com.daken.service.CommentService;

import com.daken.service.UserService;
import com.daken.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2022-10-07 15:01:46
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    /**
     * 查询 root 评论
     *
     * @param commentType
     * @param articleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        //对articleId进行
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);
        //根评论rootId为-1
        queryWrapper.eq(Comment::getRootId, SystemConstants.ROOT_ID);
        //根据评论类型进行分类
        queryWrapper.eq(Comment::getType,commentType);

        //分页查询
        Page<Comment> page=new Page(pageNum,pageSize);
        page(page,queryWrapper);

        List<CommentVo> commentVoList= toCommentVoList(page.getRecords());
        //根据评论的id查询子评论
        commentVoList = commentVoList.stream()
                .map(commentVo -> {
                    List<CommentVo> children = getChildren(commentVo.getId());
                    commentVo.setChildren(children);
                    return commentVo;
                })
                .collect(Collectors.toList());

        return ResponseResult.okResult(new PageVo(commentVoList,page.getTotal()));
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        //评论内容不能为空
        if(!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        save(comment);
        return ResponseResult.okResult();
    }

    /*
        对Vo进行操作(封装一个方法进行)
     */
    private List<CommentVo> toCommentVoList(List<Comment> list){
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);

        commentVos = commentVos.stream()
                .map(commentVo -> {
                    //通过createBy查询用户昵称
                    String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
                    commentVo.setUsername(nickName);
                    //通过toCommentUserId查询用户昵称
                    //如果toCommentUserId不为-1才进行查询
                    if (commentVo.getToCommentUserId() != -1) {
                        String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                        commentVo.setToCommentUserName(toCommentUserName);
                    }
                    return commentVo;
                })
                .collect(Collectors.toList());

        return commentVos;
    }

    /**
     * 根据根评论的id查询所对应的子评论的集合
     * @param id 根评论的id
     * @return
     */
    private List<CommentVo> getChildren(Long id){
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,id);
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> list = list(queryWrapper);

        List<CommentVo> commentVos = toCommentVoList(list);

        return commentVos;
    }

}

