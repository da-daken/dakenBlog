package com.daken.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.daken.constants.SystemConstants;
import com.daken.domain.ResponseResult;
import com.daken.domain.dto.ChangeStatusDto;
import com.daken.domain.entity.Role;
import com.daken.domain.entity.RoleMenu;
import com.daken.domain.vo.PageVo;
import com.daken.domain.vo.RoleVo;
import com.daken.mapper.RoleMapper;
import com.daken.service.RoleMenuService;
import com.daken.service.RoleService;
import com.daken.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-10-11 23:21:29
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //判断是否是管理员
        if (id == SystemConstants.ADMIN_ID){
            List<String> roleKeys=new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult selectRolePage(Role role, Integer pageNum, Integer pageSize) {
        //根据条件查询list
        LambdaQueryWrapper<Role> queryWrapper=new LambdaQueryWrapper<>();
        //要求能够针对角色名称进行模糊查询
        queryWrapper.like(StringUtils.hasText(role.getRoleName()),Role::getRoleName,role.getRoleName());
        //要求能够针对状态进行查询
        queryWrapper.eq(StringUtils.hasText(role.getStatus()),Role::getStatus,role.getStatus());
        //要求按照role_sort进行升序排列
        queryWrapper.orderByAsc(Role::getRoleSort);
        //分页
        Page<Role> page=new Page<>();
        page.setSize(pageSize);
        page.setCurrent(pageNum);
        page(page,queryWrapper);
        //封装
        List<Role> records = page.getRecords();
        List<RoleVo> roleVos = BeanCopyUtils.copyBeanList(records, RoleVo.class);
        return ResponseResult.okResult(new PageVo(roleVos,page.getTotal()));

    }

    @Override
    public ResponseResult changeStatus(ChangeStatusDto dto) {
        //根据id转换status
        Role role = BeanCopyUtils.copyBean(dto, Role.class);
        updateById(role);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult add(Role role) {
        //先更新role表
        save(role);
        //再更新role_menu表
        List<RoleMenu> roleMenuList=null;
        if(role.getMenuIds()!=null&&role.getMenuIds().length>0){
            roleMenuList = Arrays.stream(role.getMenuIds())
                    .map(o -> new RoleMenu(role.getId(), o))
                    .collect(Collectors.toList());
        }
        roleMenuService.saveBatch(roleMenuList);
        return ResponseResult.okResult();

    }

    @Override
    public ResponseResult selectRoleAll() {
        //查询的是所有状态正常的角色
        LambdaQueryWrapper<Role> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus,SystemConstants.NORMAL);
        List<Role> roleList = list(queryWrapper);
        return ResponseResult.okResult(roleList);
    }

    @Override
    public List<Long> selectRoleIdByUserId(Long id) {
        return getBaseMapper().selectRoleIdByUserId(id);
    }
}

