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
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/8/5 8:51
 **/
@RestController
@RequestMapping("menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @PostMapping("getMenuList")
    public Result getMenuList(){
        return menuService.getMenuList();
    }

    @PostMapping("addMenu")
    public Result addMenu(@RequestBody Menu menu){
        menu.setMenuId(UUID.randomUUID().toString().replace("-", ""));
        return menuService.addMenu(menu);
    }

    @PostMapping("delMenu")
    public Result delMenu(@RequestBody List<String> menuIds){
        return menuService.delMenu(menuIds);
    }

    @PostMapping("updateMenu")
    public Result updateMenu(@RequestBody Menu menu){
        return menuService.updateMenu(menu);
    }

    @PostMapping("getMenuChildrenById")
    public Result getMenuChildrenById(@RequestBody Menu menu){
        return menuService.getMenuChildrenById(menu);
    }
}
