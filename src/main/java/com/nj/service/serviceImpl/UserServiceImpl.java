package com.nj.service.serviceImpl;

import com.nj.mapper.UserMapper;
import com.nj.pojo.Enums.StatusCode;
import com.nj.pojo.User;
import com.nj.pojo.res.Result;
import com.nj.service.UserService;
import com.nj.utils.PasswordUtil;
import com.nj.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/3/6 0:26
 **/
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Value("${token.expire-time}")
    private long expireTime;

    @Override
    public Result register(User user) {
        /**
         * 判断用户名是否被注册
         */
        List<User> users = userMapper.selectOneByUserName(user.getUserName());
        if (users.size()>0){
            return Result.errorResult(StatusCode.USER_EXISTED.getCode(), StatusCode.USER_EXISTED.getDesc());
        }else {
            //生成唯一ID
            String id = UUID.randomUUID().toString().replace("-", "");
            //对密码进行加密处理
            String passwordEncryption = PasswordUtil.passwordEncryption(user.getPassword());

            //创建对象
            User paruser = new User();
            paruser.setUserId(id);
            paruser.setUserName(user.getUserName());
            paruser.setPassword(passwordEncryption);
            paruser.setRoleCode("ptyh");
            paruser.setCreateTime(new Date());
            paruser.setEmailAddress(user.getEmailAddress());
            paruser.setFullName(user.getFullName());
            userMapper.register(paruser);
            return Result.successfulResult();
        }
    }

    @Override
    public Result login(User user, HttpServletResponse response) {
        List<User> users = userMapper.selectOneByUserName(user.getUserName());
        if (users.size()>0){
            User checkUser = users.get(0);
            if (PasswordUtil.checkPassword(user.getPassword(),checkUser.getPassword())){
                //登陆成功
                String token = TokenUtil.getToken(checkUser.getUserId());//生成token
                Cookie cookie = new Cookie("token",token);//token放入Cookie
                cookie.setPath("/");
                cookie.setMaxAge(-1);
                response.addCookie(cookie);

                //将用户信息存入redis
                redisTemplate.opsForValue().set(checkUser.getUserId(),checkUser, Duration.ofMillis(expireTime));
                return Result.successfulResult(token);
            }else {
                return Result.errorResult(StatusCode.PASSWORD_ERROR.getCode(), StatusCode.PASSWORD_ERROR.getDesc());
            }
        }else {
            return Result.errorResult(StatusCode.USER_NOT_EXIST.getCode(), StatusCode.USER_NOT_EXIST.getDesc());
        }
    }

    @Override
    public Result logout(HttpServletRequest request) {
        //退出登录后的token
        String token = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("token")){
                token = cookie.getValue();
            }
        }
        //存入redis 过期时间设置与token过期时间一致
        assert token != null;
        redisTemplate.opsForValue().set(token,'0', Duration.ofMillis(expireTime));

        //删除redis中该用户的信息
        redisTemplate.delete(TokenUtil.getUserIdByToken(token));
        return Result.successfulResult();
    }

    @Override
    public Result getAllUser() {
        return Result.successfulResult(userMapper.getAllUser());
    }
}
