package com.nj.mapper;

import com.nj.pojo.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单数据访问层
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/8/2
 */
@Mapper
public interface MenuMapper {

    /**
     * 获取全部菜单列表
     *
     * @return 菜单列表
     */
    List<Menu> getMenuList();

    /**
     * 根据父级ID获取子菜单
     *
     * @param menu 包含父级ID的菜单对象
     * @return 子菜单列表
     */
    List<Menu> getMenuChildrenById(Menu menu);

    /**
     * 批量删除菜单
     *
     * @param menuIds 菜单ID列表
     */
    void delMenu(List<String> menuIds);

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     */
    void addMenu(Menu menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     */
    void updateMenu(Menu menu);
}
