package com.daken.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.daken.domain.entity.UserRole;
import com.daken.mapper.UserRoleMapper;
import com.daken.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户和角色关联表(UserRole)表服务实现类
 *
 * @author makejava
 * @since 2022-10-12 00:02:58
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}

