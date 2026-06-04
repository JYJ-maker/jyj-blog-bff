package com.nj.interceptor;

import com.nj.exception.CustomException;
import com.nj.pojo.Enums.StatusCode;
import com.nj.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/5/29 19:29
 **/
//@Component
public class OverallInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("token") && !"null".equals(cookie.getValue())){
                    token = cookie.getValue();
                }
            }
        }
        //token验证
        if(token == null){
            throw new CustomException(StatusCode.NO_TOKEN.getCode(), StatusCode.NO_TOKEN.getDesc());
        }
        if (!TokenUtil.checkToken(token)){
            throw new CustomException(StatusCode.TOKEN_EXPIRE.getCode(), StatusCode.TOKEN_EXPIRE.getDesc());
        }
        if(Boolean.FALSE.equals(redisTemplate.hasKey(token))){
            throw new CustomException(StatusCode.TOKEN_EXPIRE.getCode(),StatusCode.TOKEN_EXPIRE.getDesc());
        }
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
