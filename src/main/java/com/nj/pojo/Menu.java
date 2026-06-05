package com.nj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Describe: 菜单 Menu
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/6/30 17:31
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    /**
     * 主键
     */
    private String menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单路由地址
     */
    private String menuRouterAddress;

    /**
     * 菜单路由地址
     */
    private String menuIcon = "el-icon-s-shop";

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 菜单级别
     */
    private String menuLevel;

    /**
     * 是否可删除
     */
    private String canDelete;

    /**
     * 子
     */
    private List<Menu> children;
}
