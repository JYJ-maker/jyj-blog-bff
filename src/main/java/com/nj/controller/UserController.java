package com.nj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nj.pojo.User;
import com.nj.pojo.res.Result;
import com.nj.service.UserService;
import com.nj.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2023/12/28 9:53
 **/
@RestController
@RequestMapping("weatherSys")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @PostMapping("register")
    public Result register(@RequestBody User user){
        if (ObjectUtils.isEmpty(user.getUserName()) || ObjectUtils.isEmpty(user.getPassword())){
            return Result.paramsErrorResult();
        }
        return userService.register(user);
    }

    @PostMapping("login")
    public Result login(@RequestBody User user, HttpServletResponse response){
        if (ObjectUtils.isEmpty(user.getUserName()) || ObjectUtils.isEmpty(user.getPassword())){
            return Result.paramsErrorResult();
        }
        return userService.login(user,response);
    }

    @PostMapping("logout")
    public Result logout(HttpServletRequest request){
        return userService.logout(request);
    }

    @PostMapping("getUser")
    public Result getUser(HttpServletRequest request){
        //获取请求中的token值
        String token = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("token")){
                token = cookie.getValue();
            }
        }
        //从redis中获取用户信息
        Object o = redisTemplate.opsForValue().get(TokenUtil.getUserIdByToken(token));
        Map<String,Object> map = JSONObject.parseObject(JSON.toJSONString(o));
        //删除用户信息中的密码
        map.keySet().removeIf("password"::equals);
        System.out.println(map);
        return Result.successfulResult(map);
    }

    @PostMapping("getAllUser")
    public Result getAllUser(){
        return userService.getAllUser();
    }
}
