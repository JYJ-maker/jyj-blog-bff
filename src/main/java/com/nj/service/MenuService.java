package com.nj.service;

import com.nj.pojo.Menu;
import com.nj.pojo.res.Result;

import java.util.List;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/8/2 17:30
 **/
public interface MenuService {
    Result getMenuList();

    Result getMenuChildrenById(Menu menu);

    Result delMenu(List<String> menuIds);

    Result addMenu(Menu menu);

    Result updateMenu(Menu menu);
}
