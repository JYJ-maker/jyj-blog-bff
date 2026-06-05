package com.nj.service;

import com.nj.pojo.User;
import com.nj.pojo.res.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/3/6 0:25
 **/
public interface UserService {
    Result register(User user);

    Result login(User user, HttpServletResponse response);

    Result logout(HttpServletRequest request);

    Result getAllUser();
}
