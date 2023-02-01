package com.daken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.domain.ResponseResult;
import com.daken.domain.dto.TagListDto;
import com.daken.domain.entity.Tag;

/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2022-10-11 10:38:18
 */
public interface TagService extends IService<Tag> {


    ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult listAllTag();
}

