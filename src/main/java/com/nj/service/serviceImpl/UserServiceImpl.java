package com.nj.service.serviceImpl;

import com.nj.exception.CustomException;
import com.nj.mapper.UserMapper;
import com.nj.pojo.Enums.StatusCode;
import com.nj.pojo.User;
import com.nj.pojo.res.Result;
import com.nj.service.UserService;
import com.nj.utils.PasswordUtil;
import com.nj.utils.TokenUtil;
import com.nj.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 用户服务实现类
 * <p>
 * 实现用户注册、登录、登出及查询等核心业务逻辑。
 * 登录成功后将用户信息存入Redis，并通过Cookie传递JWT Token。
 * </p>
 *
 * @author jiayj
 * @version 2.0
 * @date 2024/3/6
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOGIN_LOCK_DURATION = 900000L;
    private static final String LOGIN_FAIL_PREFIX = "login:fail:user:";
    private static final String LOGIN_FAIL_IP_PREFIX = "login:fail:ip:";
    private static final String LOGOUT_MARKER = "0";

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Token过期时长（毫秒），从配置文件读取
     */
    @Value("${token.expire-time}")
    private long expireTime;

    /**
     * 用户注册
     * <p>
     * 流程：
     * 1. 校验用户名、密码、邮箱格式
     * 2. 检查用户名是否已存在
     * 3. 生成唯一用户ID
     * 4. 对密码进行BCrypt加密
     * 5. 设置默认角色为普通用户（ptyh）
     * 6. 写入数据库
     * </p>
     *
     * @param user 用户注册信息
     * @return 注册结果
     */
    @Override
    public Result register(User user) {
        String userNameError = ValidateUtil.validateUserName(user.getUserName());
        if (userNameError != null) {
            return Result.errorResult(StatusCode.PARAM_VALIDATE_ERROR.getCode(), userNameError);
        }

        String passwordError = ValidateUtil.validatePassword(user.getPassword());
        if (passwordError != null) {
            return Result.errorResult(StatusCode.PARAM_VALIDATE_ERROR.getCode(), passwordError);
        }

        String emailError = ValidateUtil.validateEmail(user.getEmailAddress());
        if (emailError != null) {
            return Result.errorResult(StatusCode.PARAM_VALIDATE_ERROR.getCode(), emailError);
        }

        List<User> existingUsers = userMapper.selectOneByUserName(user.getUserName().trim());
        if (!existingUsers.isEmpty()) {
            return Result.errorResult(StatusCode.USER_EXISTED.getCode(), StatusCode.USER_EXISTED.getDesc());
        }

        String userId = UUID.randomUUID().toString().replace("-", "");
        String encryptedPassword = PasswordUtil.passwordEncryption(user.getPassword());

        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUserName(user.getUserName().trim());
        newUser.setPassword(encryptedPassword);
        newUser.setRoleCode("ptyh");
        newUser.setCreateTime(new Date());
        newUser.setEmailAddress(user.getEmailAddress());
        newUser.setFullName(user.getFullName());

        userMapper.register(newUser);
        log.info("用户注册成功, userName: {}", user.getUserName());
        return Result.successfulResult();
    }

    /**
     * 用户登录
     * <p>
     * 流程：
     * 1. 检查登录频率限制（用户名和IP维度）
     * 2. 根据用户名查询用户
     * 3. 验证密码（BCrypt匹配）
     * 4. 生成JWT Token并写入HttpOnly Cookie
     * 5. 将token和用户信息存入Redis
     * 6. 登录成功时清除失败计数
     * </p>
     *
     * @param user     用户登录信息
     * @param response HTTP响应对象，用于写入Cookie
     * @return 登录结果，成功时返回token
     */
    @Override
    public Result login(User user, HttpServletResponse response) {
        String clientIp = getClientIp();

        String userFailKey = LOGIN_FAIL_PREFIX + user.getUserName();
        String ipFailKey = LOGIN_FAIL_IP_PREFIX + clientIp;

        if (isRateLimited(userFailKey) || isRateLimited(ipFailKey)) {
            return Result.errorResult(StatusCode.LOGIN_RATE_LIMITED.getCode(), StatusCode.LOGIN_RATE_LIMITED.getDesc());
        }

        List<User> users = userMapper.selectOneByUserName(user.getUserName());
        if (users.isEmpty()) {
            incrementFailCount(userFailKey);
            incrementFailCount(ipFailKey);
            return Result.errorResult(StatusCode.PASSWORD_ERROR.getCode(), "用户名或密码错误");
        }

        User dbUser = users.get(0);
        if (!PasswordUtil.checkPassword(user.getPassword(), dbUser.getPassword())) {
            incrementFailCount(userFailKey);
            incrementFailCount(ipFailKey);
            return Result.errorResult(StatusCode.PASSWORD_ERROR.getCode(), "用户名或密码错误");
        }

        redisTemplate.delete(userFailKey);
        redisTemplate.delete(ipFailKey);

        String token = TokenUtil.getToken(dbUser.getUserId());

        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        response.addCookie(cookie);

        redisTemplate.opsForValue().set(token, dbUser.getUserId(), Duration.ofMillis(expireTime));
        redisTemplate.opsForValue().set(dbUser.getUserId(), dbUser, Duration.ofMillis(expireTime));

        log.info("用户登录成功, userName: {}", user.getUserName());
        return Result.successfulResult(token);
    }

    /**
     * 用户登出
     * <p>
     * 流程：
     * 1. 从Cookie中获取当前token
     * 2. 将token标记为失效（Redis中值设为'0'）
     * 3. 删除Redis中的用户信息
     * </p>
     *
     * @param request HTTP请求对象
     * @return 登出结果
     */
    @Override
    public Result logout(HttpServletRequest request) {
        String token = TokenUtil.getTokenFromCookie(request);
        if (token == null) {
            throw new CustomException(StatusCode.NO_TOKEN.getCode(), StatusCode.NO_TOKEN.getDesc());
        }

        String userId = TokenUtil.getUserIdByToken(token);

        redisTemplate.opsForValue().set(token, LOGOUT_MARKER, Duration.ofMillis(expireTime));

        if (userId != null) {
            redisTemplate.delete(userId);
        }

        log.info("用户登出成功");
        return Result.successfulResult();
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @Override
    public Result getAllUser() {
        return Result.successfulResult(userMapper.getAllUser());
    }

    private boolean isRateLimited(String key) {
        Object count = redisTemplate.opsForValue().get(key);
        if (count == null) {
            return false;
        }
        return Integer.parseInt(count.toString()) >= MAX_LOGIN_ATTEMPTS;
    }

    private void incrementFailCount(String key) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofMillis(LOGIN_LOCK_DURATION));
        }
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                if (ip != null && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception e) {
            log.warn("获取客户端IP失败: {}", e.getMessage());
        }
        return "unknown";
    }
}
