package com.nj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Describe: 角色、菜单关联表
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/6/30 17:36
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenu {

    /**
     * 角色ID --主键
     */
    private String roleCode;

    /**
     * 菜单ID --主键
     */
    private String menuId;
}
