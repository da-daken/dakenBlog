package com.daken.service.impl;

import com.daken.domain.ResponseResult;
import com.daken.domain.entity.LoginUser;
import com.daken.domain.entity.Menu;
import com.daken.domain.entity.User;
import com.daken.domain.vo.AdminUserInfoVo;
import com.daken.domain.vo.MenuVo;
import com.daken.domain.vo.RoutersVo;
import com.daken.domain.vo.UserInfoVo;
import com.daken.service.LoginService;
import com.daken.service.MenuService;
import com.daken.service.RoleService;
import com.daken.utils.BeanCopyUtils;
import com.daken.utils.JwtUtil;
import com.daken.utils.RedisCache;
import com.daken.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SystemLoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid 生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("login:"+userId,loginUser);

        //把token封装 返回
        Map<String,String> map = new HashMap<>();
        map.put("token",jwt);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult getInfo() {
        //获取当前用户id
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //根据id查询权限
        List<String> perms = menuService.selectPermsByUserId(loginUser.getUser().getId());
        //根据id查询角色
        List<String> roles = roleService.selectRoleKeyByUserId(loginUser.getUser().getId());
        //封装
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);

        AdminUserInfoVo vo=new AdminUserInfoVo(perms,roles,userInfoVo);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult getRouters() {
        //查询menu表，结果是tree形式
        Long userId = SecurityUtils.getUserId();
        List<MenuVo> menuVos=menuService.selectRouterMenuTreeByUserId(userId);
        //封装返回
        return ResponseResult.okResult(new RoutersVo(menuVos));
    }

    @Override
    public ResponseResult logout() {
        //获取当前用户的id
        Long userId = SecurityUtils.getUserId();
        //删除redis中的信息
        redisCache.deleteObject("login:"+userId);
        return ResponseResult.okResult();
    }


}
