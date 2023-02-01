package com.daken.controller;

import com.daken.domain.ResponseResult;
import com.daken.domain.entity.User;
import com.daken.enums.AppHttpCodeEnum;
import com.daken.exception.SystemException;
import com.daken.service.UserService;
import com.daken.utils.SecurityUtils;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult list(User user, Integer pageNum, Integer pageSize){
        return userService.selectUserPage(user,pageNum,pageSize);
    }

    @PostMapping
    public ResponseResult add(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (!userService.checkUserNameUnique(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (!userService.checkPhoneUnique(user)){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        if (!userService.checkEmailUnique(user)){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        return userService.add(user);
    }

    @DeleteMapping("/{ids}")
    public ResponseResult delete(@PathVariable List<Long> ids){
        if(ids.contains(SecurityUtils.getUserId())){
            return ResponseResult.errorResult(500,"不能删除当前你正在使用的用户");
        }
        userService.removeByIds(ids);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult getUserInfoAndRoleIds(@PathVariable Long id){
        return userService.getUserInfoAndRoleIds(id);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody User user){
        return userService.updateUser(user);
    }
}
