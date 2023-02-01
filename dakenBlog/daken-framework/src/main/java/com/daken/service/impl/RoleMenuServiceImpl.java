package com.daken.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.daken.domain.entity.RoleMenu;
import com.daken.mapper.RoleMenuMapper;
import com.daken.service.RoleMenuService;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表(RoleMenu)表服务实现类
 *
 * @author makejava
 * @since 2022-10-14 13:04:11
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

}

