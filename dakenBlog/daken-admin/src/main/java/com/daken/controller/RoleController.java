package com.daken.controller;


import com.daken.domain.ResponseResult;
import com.daken.domain.dto.ChangeStatusDto;
import com.daken.domain.entity.Role;
import com.daken.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult list(Role role, Integer pageNum, Integer pageSize){
        return roleService.selectRolePage(role,pageNum,pageSize);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody ChangeStatusDto dto){
        return roleService.changeStatus(dto);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Role role){
        return roleService.add(role);
    }

    @GetMapping("/{id}")
    public ResponseResult getInfo(@PathVariable Long id){
        Role role = roleService.getById(id);
        return ResponseResult.okResult(role);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody Role role){
        roleService.updateById(role);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id){
        roleService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/listAllRole")
    public ResponseResult listAllRole(){
        return roleService.selectRoleAll();
    }
}
