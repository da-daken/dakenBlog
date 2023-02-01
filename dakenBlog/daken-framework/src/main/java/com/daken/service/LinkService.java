package com.daken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Link;

/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2022-09-28 22:35:16
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult selectLinkPage(Link link, Integer pageNum, Integer pageSize);
}

