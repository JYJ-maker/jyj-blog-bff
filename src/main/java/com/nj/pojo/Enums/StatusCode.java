package com.nj.pojo.Enums;

/**
 * 响应状态码枚举
 * <p>
 * 定义系统中所有标准化的响应状态码及对应的描述信息。
 * </p>
 * <ul>
 *   <li>2xx - 成功/业务状态</li>
 *   <li>4xx - 客户端错误（认证/授权相关）</li>
 *   <li>5xx - 服务端错误</li>
 * </ul>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/6/21
 */
public enum StatusCode {

    /** 请求成功 */
    SUCCESS_STATUS(200, "访问成功"),
    /** 用户名已存在 */
    USER_EXISTED(201, "用户名已经存在"),
    /** 密码验证失败 */
    PASSWORD_ERROR(202, "密码验证失败，请检查后重试"),
    /** 用户名不存在 */
    USER_NOT_EXIST(203, "用户名不存在"),
    /** 未携带Token */
    NO_TOKEN(400, "未检测到token,请登录"),
    /** Token已过期或失效 */
    TOKEN_EXPIRE(401, "token失效,请重新登录"),
    /** 无权限访问 */
    NO_PERMISSION(402, "无权限访问"),
    /** 服务内部异常 */
    ERROR_STATUS(500, "服务异常"),
    /** 缺少必要参数 */
    NO_MAIN_PARAMS(501, "缺失重要参数");

    private final Integer code;
    private final String desc;

    StatusCode(Integer code, String desc) {
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
