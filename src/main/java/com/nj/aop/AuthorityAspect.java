package com.nj.aop;

import com.nj.annotations.Authority;
import com.nj.exception.CustomException;
import com.nj.pojo.Enums.StatusCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 权限校验切面类
 * <p>
 * 拦截标注了 {@link Authority} 注解的方法，
 * 在方法执行前校验当前用户是否具备所需权限。
 * </p>
 * <p>
 * TODO: 当前用户权限值为硬编码（"bz"），后续应从Redis/Token中动态获取用户角色权限。
 * </p>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/5/30
 */
@Aspect
@Component
public class AuthorityAspect {

    /**
     * 权限校验前置通知
     * <p>
     * 在标注了@Authority注解的方法执行前，比较用户权限与接口所需权限。
     * </p>
     *
     * @param joinPoint 连接点
     */
    @Before("@annotation(com.nj.annotations.Authority)")
    public void checkAuthority(JoinPoint joinPoint) {
        // TODO: 应从当前登录用户的Session/Token中获取实际权限，当前为硬编码占位
        String userAuthority = "bz";

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
