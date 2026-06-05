package com.nj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nj.exception.CustomException;
import com.nj.pojo.Enums.StatusCode;
import com.nj.pojo.User;
import com.nj.pojo.res.Result;
import com.nj.service.UserService;
import com.nj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 用户管理控制器
 * <p>
 * 提供用户注册、登录、登出、查询等接口。
 * </p>
 *
 * @author jiayj
 * @version 2.0
 * @date 2023/12/28
 */
@Slf4j
@RestController
@RequestMapping("weatherSys")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param user 用户信息（需包含userName和password）
     * @return 注册结果
     */
    @PostMapping("register")
    public Result register(@RequestBody User user) {
        if (ObjectUtils.isEmpty(user.getUserName()) || ObjectUtils.isEmpty(user.getPassword())) {
            return Result.paramsErrorResult();
        }
        return userService.register(user);
    }

    /**
     * 用户登录
     *
     * @param user     用户信息（需包含userName和password）
     * @param response HTTP响应对象，用于写入Cookie
     * @return 登录结果，成功时返回token
     */
    @PostMapping("login")
    public Result login(@RequestBody User user, HttpServletResponse response) {
        if (ObjectUtils.isEmpty(user.getUserName()) || ObjectUtils.isEmpty(user.getPassword())) {
            return Result.paramsErrorResult();
        }
        return userService.login(user, response);
    }

    /**
     * 用户登出
     *
     * @param request  HTTP请求对象，用于读取Cookie中的token
     * @param response HTTP响应对象，用于清除Cookie
     * @return 登出结果
     */
    @PostMapping("logout")
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        Result result = userService.logout(request);
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return result;
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 从Cookie中获取token，解析用户ID后从Redis中读取用户信息，
     * 并移除密码字段后返回。
     * </p>
     *
     * @param request HTTP请求对象
     * @return 用户信息（不含密码）
     */
    @PostMapping("getUser")
    public Result getUser(HttpServletRequest request) {
        String token = TokenUtil.getTokenFromCookie(request);
        if (token == null) {
            throw new CustomException(StatusCode.NO_TOKEN.getCode(), StatusCode.NO_TOKEN.getDesc());
        }

        String userId = TokenUtil.getUserIdByToken(token);
        if (userId == null) {
            throw new CustomException(StatusCode.TOKEN_EXPIRE.getCode(), StatusCode.TOKEN_EXPIRE.getDesc());
        }

        Object redisUser = redisTemplate.opsForValue().get(userId);
        if (redisUser == null) {
            throw new CustomException(StatusCode.TOKEN_EXPIRE.getCode(), StatusCode.TOKEN_EXPIRE.getDesc());
        }

        Map<String, Object> userMap = JSONObject.parseObject(JSON.toJSONString(redisUser));
        userMap.remove("password");

        log.debug("获取用户信息成功, userId: {}", userId);
        return Result.successfulResult(userMap);
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @PostMapping("getAllUser")
    public Result getAllUser() {
        return userService.getAllUser();
    }
}
