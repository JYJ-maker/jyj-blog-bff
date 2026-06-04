package com.nj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nj.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/3/6 0:28
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {
    void register(User user);

    List<User> selectOneByUserName(@Param("userName") String userName);

    List<User> getAllUser();
}
