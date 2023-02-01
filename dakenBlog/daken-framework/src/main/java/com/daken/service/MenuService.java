package com.daken.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Menu;
import com.daken.domain.vo.MenuVo;
import com.daken.domain.vo.RoutersVo;

import java.util.List;

/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2022-10-11 23:09:52
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long id);


    List<MenuVo> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult getList(Menu menu);


    ResponseResult getInfo(Long id);

    ResponseResult edit(Menu menu);

    ResponseResult delete(Long menuId);

    List<Long> selectMenuListByRoleId(Long roleId);
}

