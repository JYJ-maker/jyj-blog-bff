package com.nj.exception;

import com.nj.pojo.Enums.StatusCode;
import com.nj.pojo.res.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 * <p>
 * 统一捕获并处理Controller层抛出的异常，返回标准化的错误响应。
 * </p>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/5/30
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     *
     * @param exception 自定义异常
     * @return 包含错误码和错误信息的响应结果
     */
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public Result customException(CustomException exception) {
        log.warn("业务异常: code={}, message={}", exception.getCode(), exception.getErrorMessage());
        return Result.errorResult(exception.getCode(), exception.getErrorMessage());
    }

    /**
     * 处理所有未捕获的异常
     *
     * @param exception 未知异常
     * @return 通用服务异常响应
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result allException(Exception exception) {
        log.error("系统异常: ", exception);
        return Result.errorResult(StatusCode.ERROR_STATUS.getCode(), StatusCode.ERROR_STATUS.getDesc());
    }
}
