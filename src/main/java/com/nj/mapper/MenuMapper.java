package com.nj.mapper;

import com.nj.pojo.Menu;
import com.nj.pojo.res.Result;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Describe:
 * @Version: 1.0
 * @Author: jiayj
 * @Email: jiayongjie1217@163.com
 * @Date: 2024/8/2 17:23
 **/
@Mapper
public interface MenuMapper {

    List<Menu> getMenuList();

    List<Menu> getMenuChildrenById(Menu menu);

    void delMenu(List<String> menuIds);

    void addMenu(Menu menu);

    void updateMenu(Menu menu);

}
