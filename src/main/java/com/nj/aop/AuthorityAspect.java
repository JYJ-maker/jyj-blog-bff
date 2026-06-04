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
 * @Describe: 权限注解切面类
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/5/30 15:24
 **/
@Aspect
@Component
public class AuthorityAspect {

    @Before("@annotation(com.nj.annotations.Authority)")
    public void checkAuthority(JoinPoint joinPoint){
        //1.获取用户权限信息
        String auth = "bz";
        //2.获取注解类
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Authority annotation = signature.getMethod().getAnnotation(Authority.class);
        //3.验证用户权限是否可以访问接口
        if (!checkUserAuthority(auth, annotation.value())){
            //无权限访问
            throw new CustomException(StatusCode.NO_PERMISSION.getCode(), StatusCode.NO_PERMISSION.getDesc());
        }
    }

    /**
     * @param userAuthority 用户拥有的权限
     * @param requireAuthority 接口所需要的权限
     * @return 验证结果
     */
    private boolean checkUserAuthority(String userAuthority,String requireAuthority){
        return requireAuthority.contains(userAuthority);
    }


}
