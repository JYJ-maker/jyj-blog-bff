package com.nj.exception;

import com.nj.pojo.Enums.StatusCode;
import com.nj.pojo.res.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Describe: 统一异常处理
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/5/30 22:57
 **/
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 自定义异常
     * @param exception 自定义异常类
     * @return
     */
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public Result customException(CustomException exception){
//        exception.printStackTrace();
        System.out.println(exception.getCode()+ exception.getErrorMessage());
        return Result.errorResult(exception.getCode(), exception.getErrorMessage());
    }

    /**
     * 所有异常统一处理
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result allException(Exception exception){
        exception.printStackTrace();
        return Result.errorResult(StatusCode.ERROR_STATUS.getCode(), StatusCode.ERROR_STATUS.getDesc());
    }
}
