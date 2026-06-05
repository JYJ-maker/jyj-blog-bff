package com.nj.service.serviceImpl;

import com.nj.exception.CustomException;
import com.nj.mapper.UserMapper;
import com.nj.pojo.Enums.StatusCode;
import com.nj.pojo.User;
import com.nj.pojo.res.Result;
import com.nj.service.UserService;
import com.nj.utils.PasswordUtil;
import com.nj.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
 * @version 1.0
 * @date 2024/3/6
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

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
     * 1. 检查用户名是否已存在
     * 2. 生成唯一用户ID
     * 3. 对密码进行BCrypt加密
     * 4. 设置默认角色为普通用户（ptyh）
     * 5. 写入数据库
     * </p>
     *
     * @param user 用户注册信息
     * @return 注册结果
     */
    @Override
    public Result register(User user) {
        List<User> existingUsers = userMapper.selectOneByUserName(user.getUserName());
        if (!existingUsers.isEmpty()) {
            return Result.errorResult(StatusCode.USER_EXISTED.getCode(), StatusCode.USER_EXISTED.getDesc());
        }

        String userId = UUID.randomUUID().toString().replace("-", "");
        String encryptedPassword = PasswordUtil.passwordEncryption(user.getPassword());

        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUserName(user.getUserName());
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
     * 1. 根据用户名查询用户
     * 2. 验证密码（BCrypt匹配）
     * 3. 生成JWT Token并写入Cookie
     * 4. 将用户信息存入Redis（过期时间与Token一致）
     * </p>
     *
     * @param user     用户登录信息
     * @param response HTTP响应对象，用于写入Cookie
     * @return 登录结果，成功时返回token
     */
    @Override
    public Result login(User user, HttpServletResponse response) {
        List<User> users = userMapper.selectOneByUserName(user.getUserName());
        if (users.isEmpty()) {
            return Result.errorResult(StatusCode.USER_NOT_EXIST.getCode(), StatusCode.USER_NOT_EXIST.getDesc());
        }

        User dbUser = users.get(0);
        if (!PasswordUtil.checkPassword(user.getPassword(), dbUser.getPassword())) {
            return Result.errorResult(StatusCode.PASSWORD_ERROR.getCode(), StatusCode.PASSWORD_ERROR.getDesc());
        }

        String token = TokenUtil.getToken(dbUser.getUserId());

        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);

        redisTemplate.opsForValue().set(dbUser.getUserId(), dbUser, Duration.ofMillis(expireTime));
        log.info("用户登录成功, userName: {}", user.getUserName());
        return Result.successfulResult(token);
    }

    /**
     * 用户登出
     * <p>
     * 流程：
     * 1. 从Cookie中获取当前token
     * 2. 将token标记为失效（存入Redis值为'0'，过期时间与token一致）
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

        redisTemplate.opsForValue().set(token, '0', Duration.ofMillis(expireTime));

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
}
