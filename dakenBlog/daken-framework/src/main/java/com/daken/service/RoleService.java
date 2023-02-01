package com.daken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.domain.ResponseResult;
import com.daken.domain.dto.ChangeStatusDto;
import com.daken.domain.entity.Role;

import java.util.List;

/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2022-10-11 23:21:29
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult selectRolePage(Role role, Integer pageNum, Integer pageSize);

    ResponseResult changeStatus(ChangeStatusDto dto);

    ResponseResult add(Role role);

    ResponseResult selectRoleAll();

    List<Long> selectRoleIdByUserId(Long id);
}

