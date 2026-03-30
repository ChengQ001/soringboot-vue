package com.chengq.app.service.Impl;

import com.chengq.api.entity.Menu;
import com.chengq.api.mapper.MenuMapper;
import com.chengq.app.service.interfaces.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单业务层实现类
 * 实现菜单相关的业务逻辑，使用MyBatis Plus进行数据库操作
 */
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public Menu createMenu(Menu menu) {
        try {
            log.info("Creating menu: {}", menu.getName());
            
            menuMapper.insert(menu);
            log.info("Menu created successfully: {}", menu.getName());
            return menu;
        } catch (Exception e) {
            log.error("Failed to create menu: {}", e.getMessage());
            throw new RuntimeException("Failed to create menu: " + e.getMessage());
        }
    }

    @Override
    public Menu updateMenu(Menu menu) {
        try {
            log.info("Updating menu with id: {}", menu.getId());
            
            // 查询菜单是否存在
            Menu existingMenu = menuMapper.selectById(menu.getId());
            if (existingMenu == null) {
                throw new RuntimeException("Menu not found with id: " + menu.getId());
            }
            
            menuMapper.updateById(menu);
            log.info("Menu updated successfully: {}", menu.getId());
            return menu;
        } catch (Exception e) {
            log.error("Failed to update menu: {}", e.getMessage());
            throw new RuntimeException("Failed to update menu: " + e.getMessage());
        }
    }

    @Override
    public void deleteMenu(Long id) {
        try {
            log.info("Deleting menu with id: {}", id);
            
            // 查询菜单是否存在
            Menu menu = menuMapper.selectById(id);
            if (menu == null) {
                throw new RuntimeException("Menu not found with id: " + id);
            }
            
            // 检查是否有子菜单
            List<Menu> children = menuMapper.selectChildrenByParentId(id);
            if (!children.isEmpty()) {
                throw new RuntimeException("Cannot delete menu with children");
            }
            
            menuMapper.deleteById(id);
            log.info("Menu deleted successfully: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete menu: {}", e.getMessage());
            throw new RuntimeException("Failed to delete menu: " + e.getMessage());
        }
    }

    @Override
    public Menu getMenuById(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new RuntimeException("Menu not found with id: " + id);
        }
        return menu;
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuMapper.selectAllMenus();
    }

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = menuMapper.selectAllMenus();
        return buildMenuTree(allMenus);
    }

    @Override
    public List<Menu> getRootMenus() {
        return menuMapper.selectRootMenus();
    }

    @Override
    public List<Menu> getChildrenByParentId(Long parentId) {
        return menuMapper.selectChildrenByParentId(parentId);
    }

    /**
     * 构建菜单树结构
     */
    private List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> rootMenus = new ArrayList<>();
        
        // 首先找出所有根菜单
        for (Menu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                rootMenus.add(menu);
            }
        }
        
        // 为每个根菜单递归添加子菜单
        for (Menu rootMenu : rootMenus) {
            rootMenu.setChildren(findChildren(rootMenu.getId(), menus));
        }
        
        return rootMenus;
    }

    /**
     * 递归查找子菜单
     */
    private List<Menu> findChildren(Long parentId, List<Menu> menus) {
        List<Menu> children = new ArrayList<>();
        
        for (Menu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                menu.setChildren(findChildren(menu.getId(), menus));
                children.add(menu);
            }
        }
        
        return children;
    }
}