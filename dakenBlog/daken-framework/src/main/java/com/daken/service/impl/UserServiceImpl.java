package com.daken.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Role;
import com.daken.domain.entity.User;
import com.daken.domain.entity.UserRole;
import com.daken.domain.vo.PageVo;
import com.daken.domain.vo.UserInfoAndRoleIdsVo;
import com.daken.domain.vo.UserInfoVo;
import com.daken.domain.vo.UserVo;
import com.daken.enums.AppHttpCodeEnum;
import com.daken.exception.SystemException;
import com.daken.mapper.UserMapper;
import com.daken.service.RoleService;
import com.daken.service.UserRoleService;
import com.daken.service.UserService;
import com.daken.utils.BeanCopyUtils;
import com.daken.utils.ChangeListUtils;
import com.daken.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2022-10-07 16:14:10
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询信息
        User user = getById(userId);
        //封装成userInfoVo
        UserInfoVo userInfoVo= BeanCopyUtils.copyBean(user,UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断
        //  StringUtils.hasText()可以判断 (空字符串，null)
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        //对数据是否重复的判断
        if(userNameExit(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExit(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        //存入数据库(要对密码进行加密)
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        save(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult selectUserPage(User user, Integer pageNum, Integer pageSize) {
        //根据user信息查user表
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.hasText(user.getUserName()),User::getUserName,user.getUserName());
        queryWrapper.eq(StringUtils.hasText(user.getStatus()),User::getStatus,user.getStatus());
        queryWrapper.eq(StringUtils.hasText(user.getPhonenumber()),User::getPhonenumber,user.getPhonenumber());
        //分页
        Page<User> page=new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);

        //封装
        List<User> records = page.getRecords();
        List<UserVo> userVos = BeanCopyUtils.copyBeanList(records, UserVo.class);
        return ResponseResult.okResult(new PageVo(userVos,page.getTotal()));
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getUserName,userName))==0;
    }

    @Override
    public boolean checkPhoneUnique(User user) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getPhonenumber,user.getPhonenumber()))==0;
    }

    @Override
    public boolean checkEmailUnique(User user) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getEmail,user.getEmail()))==0;
    }


    private boolean nickNameExit(String nickName) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,nickName);
        //count大于0代表存在
        return count(queryWrapper)>0;
    }

    @Override
    @Transactional
    public ResponseResult add(User user) {
        //先将user存入user表
        //新增用户时注意密码加密存储
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
        //将user喝role的关联表存入
        if(user.getRoleIds()!=null&&user.getRoleIds().length>0){
            List<UserRole> userRoles = Arrays.stream(user.getRoleIds())
                    .map(o -> new UserRole(user.getId(), o))
                    .collect(Collectors.toList());
            userRoleService.saveBatch(userRoles);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserInfoAndRoleIds(Long id) {
        //所有角色
        Object data = roleService.selectRoleAll().getData();
        List<Role> roleList = ChangeListUtils.changeList(data, Role.class);
        //用户信息
        User user = getById(id);
        //当前用户所具有的角色id列表
        List<Long> roleIds = roleService.selectRoleIdByUserId(id);
        return ResponseResult.okResult(new UserInfoAndRoleIdsVo(user,roleList,roleIds));
    }

    @Override
    @Transactional
    public ResponseResult updateUser(User user) {
        // 删除用户与角色关联
        LambdaQueryWrapper<UserRole> userRoleUpdateWrapper = new LambdaQueryWrapper<>();
        userRoleUpdateWrapper.eq(UserRole::getUserId,user.getId());
        userRoleService.remove(userRoleUpdateWrapper);
        // 新增用户与角色管理
        List<UserRole> userRoleList = Arrays.stream(user.getRoleIds())
                .map(roleId -> new UserRole(user.getId(), roleId))
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoleList,1000);
        // 更新用户信息
        updateById(user);
        return ResponseResult.okResult();
    }

    private boolean userNameExit(String userName) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        //count大于0代表存在
        return count(queryWrapper)>0;
    }


}

