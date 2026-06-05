package com.nj.interceptor;

import com.nj.exception.CustomException;
import com.nj.pojo.Enums.StatusCode;
import com.nj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局请求拦截器
 * <p>
 * 对需要鉴权的请求进行Token校验，验证流程：
 * 1. 从Cookie中提取Token
 * 2. 验证Token签名及有效期
 * 3. 检查Token是否已被登出标记（Redis中token值为'0'表示已登出）
 * </p>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/5/29
 */
@Slf4j
public class OverallInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 请求预处理 - Token鉴权
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @return true-放行，false-拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = TokenUtil.getTokenFromCookie(request);

        if (token == null) {
            throw new CustomException(StatusCode.NO_TOKEN.getCode(), StatusCode.NO_TOKEN.getDesc());
        }

        if (!TokenUtil.checkToken(token)) {
            throw new CustomException(StatusCode.TOKEN_EXPIRE.getCode(), StatusCode.TOKEN_EXPIRE.getDesc());
        }

        if (Boolean.FALSE.equals(redisTemplate.hasKey(token))) {
            throw new CustomException(StatusCode.TOKEN_EXPIRE.getCode(), StatusCode.TOKEN_EXPIRE.getDesc());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) {
    }
}
