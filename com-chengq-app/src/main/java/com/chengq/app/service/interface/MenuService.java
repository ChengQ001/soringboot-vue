package com.chengq.app.service.interfaces;

import com.chengq.api.entity.Menu;

import java.util.List;

/**
 * 菜单业务层接口
 * 定义菜单相关的业务逻辑方法
 */
public interface MenuService {
    
    /**
     * 创建新菜单
     */
    Menu createMenu(Menu menu);
    
    /**
     * 更新菜单信息
     */
    Menu updateMenu(Menu menu);
    
    /**
     * 删除菜单
     */
    void deleteMenu(Long id);
    
    /**
     * 根据ID查询菜单
     */
    Menu getMenuById(Long id);
    
    /**
     * 查询所有菜单
     */
    List<Menu> getAllMenus();
    
    /**
     * 查询菜单树结构
     */
    List<Menu> getMenuTree();
    
    /**
     * 查询根菜单
     */
    List<Menu> getRootMenus();
    
    /**
     * 查询子菜单
     */
    List<Menu> getChildrenByParentId(Long parentId);
}