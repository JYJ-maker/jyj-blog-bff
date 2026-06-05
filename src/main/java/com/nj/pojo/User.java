package com.nj.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Describe: 用户表 user
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/3/6 0:22
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * 主键
     */
    private String userId;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户邮箱
     */
    private String emailAddress;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 关联角色ID
     */
    private String roleCode;

    /**
     * 关联角色名称
     */
    private String roleName;

}
