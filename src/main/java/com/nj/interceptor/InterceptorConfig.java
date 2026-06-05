package com.nj.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 * <p>
 * 根据配置项 token.requireToken 决定是否启用Token鉴权拦截。
 * 启用时拦截所有请求（登录和注册接口除外），
 * 未启用时放行所有请求。
 * </p>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/5/29
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     * 是否开启Token验证
     */
    @Value("${token.requireToken}")
    private boolean requireToken;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (requireToken) {
            registry.addInterceptor(overallInterceptor())
                    .excludePathPatterns("/weatherSys/login")
                    .excludePathPatterns("/weatherSys/register")
                    .addPathPatterns("/**");
        } else {
            registry.addInterceptor(overallInterceptor())
                    .excludePathPatterns("/**");
        }
    }

    @Bean
    public OverallInterceptor overallInterceptor() {
        return new OverallInterceptor();
    }
}
