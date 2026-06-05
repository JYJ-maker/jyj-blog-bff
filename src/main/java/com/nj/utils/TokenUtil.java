package com.nj.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nj.pojo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * @Describe: token生成验证工具
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/5/29 16:51
 **/
@Component
public class TokenUtil {
    //密钥盐值
    private static String TOKEN_SECRET;
    /**
     * 过期时长
     */
    private static long EXPIRE_TIME;

    @Value("${token.token-secret}")
    private void setTokenSecret(String tokenSecret){
        TokenUtil.TOKEN_SECRET = tokenSecret;
    }
    @Value("${token.expire-time}")
    private void setExpireTime(long expireTime){
        TokenUtil.EXPIRE_TIME = expireTime;
    }

    /**
     * 生成Token
     */
    public static String getToken(String userId){
        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME); //过期时间
        return JWT.create()
                .withClaim("userId", userId)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(TOKEN_SECRET));
    }

    /**
     * 验证Token
     */
    public static Boolean checkToken(String token){
        try {
            JWTVerifier ver = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            ver.verify(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 从token获取用户信息
     * @param
     */
    public static String getUserIdByToken(String token){
        JWTVerifier ver = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
        DecodedJWT jwt = ver.verify(token);
        return jwt.getClaim("userId").asString();
    }


}
