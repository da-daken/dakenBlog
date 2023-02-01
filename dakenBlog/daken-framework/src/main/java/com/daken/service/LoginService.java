package com.daken.service;

import com.daken.domain.ResponseResult;
import com.daken.domain.entity.User;

public interface LoginService {


    ResponseResult login(User user);

    ResponseResult getInfo();

    ResponseResult getRouters();

    ResponseResult logout();
}
