package com.nj.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @Describe: 密码加密、验证工具 (使用BCrypt加密)
 * @Version: 2.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/6/30 13:37
 **/
@Component
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 密码加密
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String passwordEncryption(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return ENCODER.encode(password);
    }

    /**
     * 密码验证
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 验证结果
     */
    public static boolean checkPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, encodedPassword);
    }

}
