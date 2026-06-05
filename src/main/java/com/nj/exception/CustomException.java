package com.nj.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Describe: 自定义权限异常
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/5/30 17:05
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CustomException extends RuntimeException{
    /**
     * 异常编码
     */
    private Integer code;
    /**
     * 异常信息
     */
    private String errorMessage;
}
