package com.daken.service;

import com.daken.domain.ResponseResult;
import com.daken.domain.entity.User;

public interface BlogLoginService {

    ResponseResult login(User user);

    ResponseResult logout();
}
