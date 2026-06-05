package com.nj.service;

import com.nj.pojo.Menu;
import com.nj.pojo.res.Result;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/8/2
 */
public interface MenuService {

    /**
     * 获取全部菜单列表（树形结构）
     *
     * @return 菜单树
     */
    Result getMenuList();

    /**
     * 根据菜单ID获取子菜单列表
     *
     * @param menu 包含父级ID的菜单对象
     * @return 子菜单列表
     */
    Result getMenuChildrenById(Menu menu);

    /**
     * 批量删除菜单
     *
     * @param menuIds 菜单ID列表
     * @return 删除结果
     */
    Result delMenu(List<String> menuIds);

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 新增结果
     */
    Result addMenu(Menu menu);

    /**
     * 更新菜单信息
     *
     * @param menu 菜单信息
     * @return 更新结果
     */
    Result updateMenu(Menu menu);
}
