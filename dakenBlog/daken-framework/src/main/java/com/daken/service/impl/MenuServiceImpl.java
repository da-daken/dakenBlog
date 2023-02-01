package com.daken.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.daken.constants.SystemConstants;
import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Menu;
import com.daken.domain.vo.MenuInfoVo;
import com.daken.domain.vo.MenuListVo;
import com.daken.domain.vo.MenuVo;
import com.daken.mapper.MenuMapper;
import com.daken.service.MenuService;
import com.daken.service.UserRoleService;
import com.daken.utils.BeanCopyUtils;
import com.daken.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2022-10-11 23:09:53
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public List<String> selectPermsByUserId(Long id) {
        //如果是管理员，返回所有的权限
        if(id == SystemConstants.ADMIN_ID){
            LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.in(Menu::getMenuType,SystemConstants.MENU,SystemConstants.BUTTON);
            queryWrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            List<Menu> menuList = list(queryWrapper);
            List<String> perms = menuList.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        } else {
            //不是管理员
            return getBaseMapper().selectPermsByUserId(id);
        }

    }

    @Override
    public List<MenuVo> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus=null;
        List<MenuVo> menuVos=null;
        //判断是否是管理员
        if(SecurityUtils.isAdmin()){
            //是的话 获取全部menu
            menus = menuMapper.selectAllRouterMenu();
            menuVos = BeanCopyUtils.copyBeanList(menus, MenuVo.class);

        } else {
            //不是的话 获取当前用户的menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
            menuVos = BeanCopyUtils.copyBeanList(menus, MenuVo.class);

        }
        //转换成tree
        List<MenuVo> menuTree = builderMenuTree(menuVos,0L);

        return menuTree;
    }



    /**
     * 构建tree
     *
     * @param menuVos
     * @param parentId
     * @return
     */
    private List<MenuVo> builderMenuTree(List<MenuVo> menuVos, long parentId) {
        List<MenuVo> menuTree = menuVos.stream()
                .filter(menuVo -> menuVo.getParentId().equals(parentId))
                .map(menuVo -> menuVo.setChildren(getChildren(menuVo, menuVos)))
                .collect(Collectors.toList());
        return menuTree;
    }

    /**
     *  获取存入参数的子Menu集合
     * @param menuVo
     * @param menuVos
     * @return
     */
    private List<MenuVo> getChildren(MenuVo menuVo, List<MenuVo> menuVos) {
        List<MenuVo> childrenList = menuVos.stream()
                .filter(m -> m.getParentId().equals(menuVo.getId()))
                .map(m -> m.setChildren(getChildren(m,menuVos)))
                .collect(Collectors.toList());
        return childrenList;
    }

    @Override
    public ResponseResult getList(Menu menu) {
        //对条件进行模糊查询
        LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(menu.getMenuName()),Menu::getMenuName,menu.getMenuName());
        queryWrapper.eq(StringUtils.hasText(menu.getStatus()),Menu::getStatus,menu.getStatus());
        //排序 parent_id和order_num
        queryWrapper.orderByAsc(Menu::getParentId,Menu::getOrderNum);
        List<Menu> menuList = list(queryWrapper);
        //封装
            List<MenuListVo> menuListVos = BeanCopyUtils.copyBeanList(menuList, MenuListVo.class);
        return ResponseResult.okResult(menuListVos);
    }

    @Override
    public ResponseResult getInfo(Long id) {
        //根据id获取menu信息
        Menu menu = getById(id);
        //封装
        MenuInfoVo menuInfoVo = BeanCopyUtils.copyBean(menu, MenuInfoVo.class);
        return ResponseResult.okResult(menuInfoVo);

    }

    @Override
    public ResponseResult edit(Menu menu) {
        //能够修改菜单，但是修改的时候不能把父菜单设置为当前菜单，如果设置了需要给出相应的提示。并且修改失败
        if(menu.getId().equals(menu.getParentId())){
            return ResponseResult.errorResult(500,"修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        updateById(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult delete(Long menuId) {
        //能够删除菜单，但是如果要删除的菜单有子菜单则提示：存在子菜单不允许删除 并且删除失败
        LambdaQueryWrapper<Menu> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,menuId);
        if(count(queryWrapper) != 0){
            return ResponseResult.errorResult(500,"存在子菜单不允许删除");
        } else {
            removeById(menuId);
        }
        return ResponseResult.okResult();
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return getBaseMapper().selectMenuListByRoleId(roleId);
    }


}

