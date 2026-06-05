package com.nj.service;

import com.nj.pojo.User;
import com.nj.pojo.res.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户服务接口
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/3/6
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param user 用户注册信息
     * @return 注册结果
     */
    Result register(User user);

    /**
     * 用户登录
     *
     * @param user     用户登录信息
     * @param response HTTP响应对象，用于写入Cookie
     * @return 登录结果
     */
    Result login(User user, HttpServletResponse response);

    /**
     * 用户登出
     *
     * @param request HTTP请求对象，用于读取Cookie
     * @return 登出结果
     */
    Result logout(HttpServletRequest request);

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    Result getAllUser();
}
