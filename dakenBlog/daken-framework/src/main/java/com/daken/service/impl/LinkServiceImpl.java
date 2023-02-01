package com.daken.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daken.constants.SystemConstants;
import com.daken.domain.ResponseResult;
import com.daken.domain.vo.LinkPageVo;
import com.daken.domain.vo.LinkVo;
import com.daken.domain.vo.PageVo;
import com.daken.mapper.LinkMapper;
import com.daken.domain.entity.Link;
import com.daken.service.LinkService;
import com.daken.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2022-09-28 22:35:17
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        //查询所以审核通过的友链
        LambdaQueryWrapper<Link> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> linkList = list(queryWrapper);
        //转换成VO
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(linkList, LinkVo.class);
        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult selectLinkPage(Link link, Integer pageNum, Integer pageSize) {
        //根据link查表
        LambdaQueryWrapper<Link> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(link.getName()),Link::getName,link.getName());
        queryWrapper.eq(Objects.nonNull(link.getStatus()),Link::getStatus,link.getStatus());
        //分页
        Page<Link> page=new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        //封装
        List<Link> records = page.getRecords();
        List<LinkPageVo> linkPageVos = BeanCopyUtils.copyBeanList(records, LinkPageVo.class);
        return ResponseResult.okResult(new PageVo(linkPageVos,page.getTotal()));
    }
}

