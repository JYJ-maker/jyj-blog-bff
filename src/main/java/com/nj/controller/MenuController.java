package com.nj.controller;

import com.nj.pojo.Menu;
import com.nj.pojo.res.Result;
import com.nj.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 菜单管理控制器
 * <p>
 * 提供菜单的增删改查及树形结构查询接口。
 * </p>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/8/5
 */
@RestController
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单列表（树形结构）
     *
     * @return 菜单列表
     */
    @PostMapping("getMenuList")
    public Result getMenuList() {
        return menuService.getMenuList();
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 新增结果
     */
    @PostMapping("addMenu")
    public Result addMenu(@RequestBody Menu menu) {
        menu.setMenuId(UUID.randomUUID().toString().replace("-", ""));
        return menuService.addMenu(menu);
    }

    /**
     * 批量删除菜单
     *
     * @param menuIds 待删除的菜单ID列表
     * @return 删除结果
     */
    @PostMapping("delMenu")
    public Result delMenu(@RequestBody List<String> menuIds) {
        return menuService.delMenu(menuIds);
    }

    /**
     * 更新菜单信息
     *
     * @param menu 菜单信息
     * @return 更新结果
     */
    @PostMapping("updateMenu")
    public Result updateMenu(@RequestBody Menu menu) {
        return menuService.updateMenu(menu);
    }

    /**
     * 根据菜单ID获取子菜单列表
     *
     * @param menu 包含parentId的菜单对象
     * @return 子菜单列表
     */
    @PostMapping("getMenuChildrenById")
    public Result getMenuChildrenById(@RequestBody Menu menu) {
        return menuService.getMenuChildrenById(menu);
    }
}
