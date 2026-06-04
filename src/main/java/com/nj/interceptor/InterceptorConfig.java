package com.nj.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/5/29 19:39
 **/
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Value("${token.requireToken}")
    private boolean requireToken; //是否开启token验证
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if(requireToken){
            registry.addInterceptor(overallInterceptor())
                    .excludePathPatterns("/weatherSys/login")
                    .excludePathPatterns("/weatherSys/register") //忽略登录、注册请求
                    .addPathPatterns("/**");//拦截所有请求
        }else {
            registry.addInterceptor(overallInterceptor())
                    .excludePathPatterns("/**"); //忽略所有请求
        }

    }

    @Bean
    public OverallInterceptor overallInterceptor(){
        return new OverallInterceptor();
    }

}
