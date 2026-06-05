package com.nj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nj.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/3/6
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 新增用户
     *
     * @param user 用户信息
     */
    void register(User user);

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return 匹配的用户列表
     */
    List<User> selectOneByUserName(@Param("userName") String userName);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> getAllUser();
}
