package com.nj.utils;

/**
 * 输入校验工具类
 * <p>
 * 提供用户名、密码、邮箱等格式校验方法。
 * </p>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/6/30
 */
public class ValidateUtil {

    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 20;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 32;

    /**
     * 校验用户名格式
     *
     * @param userName 用户名
     * @return 校验失败返回错误信息，校验通过返回null
     */
    public static String validateUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return "用户名不能为空";
        }
        String trimmed = userName.trim();
        if (trimmed.length() < USERNAME_MIN_LENGTH || trimmed.length() > USERNAME_MAX_LENGTH) {
            return "用户名长度需在" + USERNAME_MIN_LENGTH + "-" + USERNAME_MAX_LENGTH + "个字符之间";
        }
        if (!trimmed.matches("^[a-zA-Z0-9_]+$")) {
            return "用户名只能包含字母、数字和下划线";
        }
        return null;
    }

    /**
     * 校验密码强度
     *
     * @param password 密码
     * @return 校验失败返回错误信息，校验通过返回null
     */
    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            return "密码长度需在" + PASSWORD_MIN_LENGTH + "-" + PASSWORD_MAX_LENGTH + "个字符之间";
        }
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            return "密码需同时包含字母和数字";
        }
        return null;
    }

    /**
     * 校验邮箱格式
     *
     * @param email 邮箱地址
     * @return 校验失败返回错误信息，校验通过或邮箱为空时返回null（邮箱非必填）
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        if (!email.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) {
            return "邮箱格式不正确";
        }
        return null;
    }
}
