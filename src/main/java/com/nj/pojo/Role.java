package com.nj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Describe: 角色表 role
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/6/30 17:28
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    /**
     * 主键
     */
    private String roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色编码 默认配置 超级管理员（cjgly）角色
     * 账号：superAdmin 、 密码：superAdmin
     */
    private String roleCode;
}
