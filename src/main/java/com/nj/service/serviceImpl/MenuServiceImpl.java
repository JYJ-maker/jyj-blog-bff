package com.nj.service.serviceImpl;

import com.nj.mapper.MenuMapper;
import com.nj.pojo.Menu;
import com.nj.pojo.res.Result;
import com.nj.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/8/2 17:30
 **/
@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Override
    public Result getMenuList() {
        return Result.successfulResult(menuMapper.getMenuList());
    }

    @Override
    public Result getMenuChildrenById(Menu menu) {
        return Result.successfulResult(menuMapper.getMenuChildrenById(menu));
    }

    @Override
    public Result delMenu(List<String> menuIds) {
        menuMapper.delMenu(menuIds);
        return Result.successfulResult();
    }

    @Override
    public Result addMenu(Menu menu) {
        menuMapper.addMenu(menu);
        return Result.successfulResult();
    }

    @Override
    public Result updateMenu(Menu menu) {
        menuMapper.updateMenu(menu);
        return Result.successfulResult();
    }
}
