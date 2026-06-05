package com.nj.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nj.annotations.Authority;
import com.nj.exception.CustomException;
import com.nj.pojo.Enums.StatusCode;
import com.nj.utils.TokenUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 权限校验切面类
 * <p>
 * 拦截标注了 {@link Authority} 注解的方法，
 * 在方法执行前校验当前用户是否具备所需权限。
 * 通过Token从Redis中动态获取用户角色进行权限比对。
 * </p>
 *
 * @author jiayj
 * @version 2.0
 * @date 2024/5/30
 */
@Aspect
@Component
public class AuthorityAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 权限校验前置通知
     * <p>
     * 在标注了@Authority注解的方法执行前，从Redis获取当前用户角色并与接口所需权限比对。
     * </p>
     *
     * @param joinPoint 连接点
     */
    @Before("@annotation(com.nj.annotations.Authority)")
    public void checkAuthority(JoinPoint joinPoint) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new CustomException(StatusCode.NO_TOKEN.getCode(), StatusCode.NO_TOKEN.getDesc());
        }

        HttpServletRequest request = attrs.getRequest();
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
        String userAuthority = userMap.get("roleCode") != null ? userMap.get("roleCode").toString() : "";

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Authority annotation = signature.getMethod().getAnnotation(Authority.class);

        if (!checkUserAuthority(userAuthority, annotation.value())) {
            throw new CustomException(StatusCode.NO_PERMISSION.getCode(), StatusCode.NO_PERMISSION.getDesc());
        }
    }

    /**
     * 校验用户权限是否满足接口要求
     *
     * @param userAuthority    用户拥有的权限标识
     * @param requireAuthority 接口所需的权限标识
     * @return true-有权限，false-无权限
     */
    private boolean checkUserAuthority(String userAuthority, String requireAuthority) {
        return requireAuthority.contains(userAuthority);
    }
}
