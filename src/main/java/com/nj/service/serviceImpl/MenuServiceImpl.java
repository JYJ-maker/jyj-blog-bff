package com.nj.service.serviceImpl;

import com.nj.mapper.MenuMapper;
import com.nj.pojo.Menu;
import com.nj.pojo.res.Result;
import com.nj.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单服务实现类
 * <p>
 * 实现菜单的增删改查业务逻辑，支持树形菜单结构。
 * </p>
 *
 * @author jiayj
 * @version 1.0
 * @date 2024/8/2
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 获取全部菜单列表（树形结构）
     *
     * @return 菜单树
     */
    @Override
    public Result getMenuList() {
        return Result.successfulResult(menuMapper.getMenuList());
    }

    /**
     * 根据菜单ID获取子菜单
     *
     * @param menu 包含父级ID的菜单对象
     * @return 子菜单列表
     */
    @Override
    public Result getMenuChildrenById(Menu menu) {
        return Result.successfulResult(menuMapper.getMenuChildrenById(menu));
    }

    /**
     * 批量删除菜单
     *
     * @param menuIds 菜单ID列表
     * @return 删除结果
     */
    @Override
    public Result delMenu(List<String> menuIds) {
        menuMapper.delMenu(menuIds);
        return Result.successfulResult();
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 新增结果
     */
    @Override
    public Result addMenu(Menu menu) {
        menuMapper.addMenu(menu);
        return Result.successfulResult();
    }

    /**
     * 更新菜单信息
     *
     * @param menu 菜单信息
     * @return 更新结果
     */
    @Override
    public Result updateMenu(Menu menu) {
        menuMapper.updateMenu(menu);
        return Result.successfulResult();
    }
}
