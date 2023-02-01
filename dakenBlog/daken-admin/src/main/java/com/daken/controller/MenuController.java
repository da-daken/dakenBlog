package com.daken.controller;


import com.alibaba.fastjson.JSONObject;
import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Menu;
import com.daken.domain.vo.MenuListVo;
import com.daken.domain.vo.MenuTreeVo;
import com.daken.domain.vo.RoleMenuTreeSelectVo;
import com.daken.service.MenuService;
import com.daken.utils.ChangeListUtils;
import com.daken.utils.SystemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult list(Menu menu){
        return menuService.getList(menu);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Menu menu){
        menuService.save(menu);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult edit(@PathVariable Long id){
        return menuService.getInfo(id);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody Menu menu){
        return menuService.edit(menu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseResult delete(@PathVariable Long menuId){
        return menuService.delete(menuId);
    }


    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public ResponseResult roleMenuTreeSelect(@PathVariable("roleId") Long roleId){
        //全部菜单
        List<MenuListVo> list = ChangeListUtils.changeList(menuService.getList(new Menu()).getData(), MenuListVo.class);
        List<MenuTreeVo> menuTreeVos = SystemConverter.buildMenuSelectTree(list);
        //角色所关联的菜单权限id列表
        List<Long> checkedKeys=menuService.selectMenuListByRoleId(roleId);
        return ResponseResult.okResult(new RoleMenuTreeSelectVo(checkedKeys,menuTreeVos));
    }


    @GetMapping("/treeselect")
    public ResponseResult treeselect(){
        //显示所以的菜单供选择
        List<MenuListVo> list = ChangeListUtils.changeList(menuService.getList(new Menu()).getData(), MenuListVo.class);
        List<MenuTreeVo> menuTreeVos = SystemConverter.buildMenuSelectTree(list);
        return ResponseResult.okResult(menuTreeVos);

    }


}
