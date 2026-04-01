package com.chengq.app.service.interfaces;

import com.chengq.api.entity.Menu;
import java.util.List;

/**
 * 菜单业务层接口
 */
public interface IMenuService {

    Menu createMenu(Menu menu);

    Menu updateMenu(Menu menu);

    void deleteMenu(Long id);

    Menu getMenuById(Long id);

    List<Menu> getAllMenus();

    List<Menu> getMenuTree();

    List<Menu> getRootMenus();

    List<Menu> getChildrenByParentId(Long parentId);
}
