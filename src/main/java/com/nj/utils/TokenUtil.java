package com.nj.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Token生成与验证工具类
 * <p>
 * 基于JWT实现token的生成、验证和用户信息提取。
 * 通过配置文件注入密钥和过期时长。
 * </p>
 *
 * @author jiayj
 * @version 2.0
 * @date 2024/5/29
 */
@Slf4j
@Component
public class TokenUtil {

    private static final String COOKIE_TOKEN_NAME = "token";

    /**
     * JWT签名密钥
     */
    private static String TOKEN_SECRET;

    /**
     * Token过期时长（毫秒）
     */
    private static long EXPIRE_TIME;

    @Value("${token.token-secret}")
    private void setTokenSecret(String tokenSecret) {
        TokenUtil.TOKEN_SECRET = tokenSecret;
    }

    @Value("${token.expire-time}")
    private void setExpireTime(long expireTime) {
        TokenUtil.EXPIRE_TIME = expireTime;
    }

    /**
     * 生成Token
     *
     * @param userId 用户ID
     * @return JWT token字符串
     */
    public static String getToken(String userId) {
        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withClaim("userId", userId)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(TOKEN_SECRET));
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT token字符串
     * @return true-有效，false-无效或已过期
     */
    public static Boolean checkToken(String token) {
        try {
            JWTVerifier ver = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            ver.verify(token);
            return true;
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从Token中提取用户ID
     *
     * @param token JWT token字符串
     * @return 用户ID，解析失败时返回null
     */
    public static String getUserIdByToken(String token) {
        try {
            JWTVerifier ver = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            DecodedJWT jwt = ver.verify(token);
            return jwt.getClaim("userId").asString();
        } catch (Exception e) {
            log.error("从Token解析用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从HTTP请求的Cookie中提取Token值
     *
     * @param request HTTP请求对象
     * @return Cookie中的token值，未找到时返回null
     */
    public static String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_TOKEN_NAME.equals(cookie.getName()) && !"null".equals(cookie.getValue())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
