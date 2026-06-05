package com.nj.pojo.Enums;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/6/21 11:26
 **/
public enum StatusCode {
    SUCCESS_STATUS(200,"访问成功"),
    USER_EXISTED(201,"用户名已经存在"),
    PASSWORD_ERROR(202,"密码验证失败，请检查后重试"),
    USER_NOT_EXIST(203,"用户名不存在"),
    NO_TOKEN(400,"未检测到token,请登录"),
    TOKEN_EXPIRE(401,"token失效,请重新登录"),
    NO_PERMISSION(402,"无权限访问"),
    ERROR_STATUS(500,"服务异常"),
    NO_MAIN_PARAMS(501,"缺失重要参数");
    private final Integer code;

    private final String desc;

    StatusCode(Integer code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }


}
