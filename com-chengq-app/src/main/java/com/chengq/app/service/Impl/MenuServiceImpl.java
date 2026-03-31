package com.chengq.app.service.Impl;

import com.chengq.api.entity.Menu;
import com.chengq.api.entity.Role;
import com.chengq.api.entity.RoleMenu;
import com.chengq.api.mapper.MenuMapper;
import com.chengq.api.mapper.RoleMenuMapper;
import com.chengq.api.mapper.RoleMapper;
import com.chengq.app.service.interfaces.MenuService;
import com.chengq.app.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 菜单业务层实现类
 * 实现菜单相关的业务逻辑，使用MyBatis Plus进行数据库操作
 */
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;

    public MenuServiceImpl(MenuMapper menuMapper, RoleMapper roleMapper, RoleMenuMapper roleMenuMapper) {
        this.menuMapper = menuMapper;
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public Menu createMenu(Menu menu) {
        try {
            log.info("Creating menu: {}", menu.getName());
            applyTwoLevelAndSort(menu, null);
            menuMapper.insert(menu);
            log.info("Menu created successfully: {}", menu.getName());
            return menu;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create menu: {}", e.getMessage());
            throw new RuntimeException("Failed to create menu: " + e.getMessage());
        }
    }

    @Override
    public Menu updateMenu(Menu menu) {
        try {
            log.info("Updating menu with id: {}", menu.getId());

            Menu existingMenu = menuMapper.selectById(menu.getId());
            if (existingMenu == null) {
                throw new RuntimeException("Menu not found with id: " + menu.getId());
            }

            applyTwoLevelAndSort(menu, existingMenu);
            menuMapper.updateById(menu);
            log.info("Menu updated successfully: {}", menu.getId());
            return menu;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update menu: {}", e.getMessage());
            throw new RuntimeException("Failed to update menu: " + e.getMessage());
        }
    }

    /**
     * 仅允许两级：根（parent 空）与其直接子菜单；子菜单的父级必须是一级菜单。
     * sortOrder：新增时若未传则取同父下 max+10；更新时若未传则保留原值。
     */
    private void applyTwoLevelAndSort(Menu menu, Menu existing) {
        Long pid = normalizeParentId(menu.getParentId());
        menu.setParentId(pid);

        if (existing != null) {
            List<Menu> children = menuMapper.selectChildrenByParentId(menu.getId());
            if (!children.isEmpty() && pid != null) {
                throw new RuntimeException("该菜单下已有子节点，不能改为子菜单（仅支持两级）");
            }
        }

        if (pid != null) {
            if (existing != null && pid.equals(menu.getId())) {
                throw new RuntimeException("父菜单不能为自身");
            }
            Menu parent = menuMapper.selectById(pid);
            if (parent == null) {
                throw new RuntimeException("父菜单不存在");
            }
            if (!isRootMenu(parent)) {
                throw new RuntimeException("仅支持两级菜单：子菜单的父级必须是一级菜单");
            }
        }

        if (menu.getSortOrder() == null) {
            if (existing != null) {
                menu.setSortOrder(existing.getSortOrder() != null ? existing.getSortOrder() : 0);
            } else {
                menu.setSortOrder(nextSortOrder(pid));
            }
        }
    }

    private static Long normalizeParentId(Long parentId) {
        if (parentId == null || parentId == 0L) {
            return null;
        }
        return parentId;
    }

    private static boolean isRootMenu(Menu m) {
        return m != null && (m.getParentId() == null || m.getParentId() == 0L);
    }

    private int nextSortOrder(Long parentId) {
        LambdaQueryWrapper<Menu> w = new LambdaQueryWrapper<>();
        Long pid = normalizeParentId(parentId);
        if (pid == null) {
            w.and(x -> x.isNull(Menu::getParentId).or().eq(Menu::getParentId, 0L));
        } else {
            w.eq(Menu::getParentId, pid);
        }
        List<Menu> siblings = menuMapper.selectList(w);
        int max = -10;
        for (Menu s : siblings) {
            if (s != null && s.getSortOrder() != null && s.getSortOrder() > max) {
                max = s.getSortOrder();
            }
        }
        return max + 10;
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
        // 1) ADMIN：直接返回全量菜单树
        if (UserContext.hasRole("ADMIN")) {
            List<Menu> allMenus = menuMapper.selectAllMenus();
            return buildMenuTree(allMenus);
        }

        // 2) 非 ADMIN：根据当前用户角色 -> tb_role_menu -> 允许的菜单集合（含祖先+后代级联）
        List<String> currentRoleNames = UserContext.getCurrentUserRoles();
        if (currentRoleNames == null || currentRoleNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<Role> roles = roleMapper.selectList(
                new LambdaQueryWrapper<Role>()
                        .in(Role::getName, currentRoleNames)
        );

        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = roles.stream()
                .map(Role::getId)
                .filter(id -> id != null)
                .toList();
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .in(RoleMenu::getRoleId, roleIds)
        );

        Set<Long> mappedMenuIds = new HashSet<>();
        if (roleMenus != null) {
            for (RoleMenu rm : roleMenus) {
                if (rm != null && rm.getMenuId() != null) {
                    mappedMenuIds.add(rm.getMenuId());
                }
            }
        }

        if (mappedMenuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3) 构建菜单索引：parentId -> children
        List<Menu> allMenus = menuMapper.selectAllMenus();
        Map<Long, Menu> byId = new HashMap<>();
        Map<Long, List<Menu>> childrenByParentId = new HashMap<>();
        for (Menu m : allMenus) {
            if (m == null || m.getId() == null) continue;
            byId.put(m.getId(), m);

            Long pid = m.getParentId();
            if (pid != null && pid != 0) {
                childrenByParentId.computeIfAbsent(pid, k -> new ArrayList<>()).add(m);
            }
        }

        // 4) 允许集合：映射菜单 + 祖先 + 后代（允许 root 则允许其所有子菜单）
        Set<Long> allowedMenuIds = new HashSet<>();
        for (Long mappedId : mappedMenuIds) {
            addAncestors(mappedId, byId, allowedMenuIds);
            addDescendants(mappedId, childrenByParentId, allowedMenuIds);
        }

        // 5) 过滤并构建树
        List<Menu> filtered = new ArrayList<>();
        for (Menu m : allMenus) {
            if (m != null && m.getId() != null && allowedMenuIds.contains(m.getId())) {
                filtered.add(m);
            }
        }
        return buildMenuTree(filtered);
    }

    /**
     * 将指定菜单的所有祖先加入 allowedMenuIds
     */
    private void addAncestors(Long menuId, Map<Long, Menu> byId, Set<Long> allowedMenuIds) {
        if (menuId == null) return;
        Long curId = menuId;
        while (curId != null) {
            Menu cur = byId.get(curId);
            if (cur == null) return;
            Long pid = cur.getParentId();
            if (pid == null || pid == 0) {
                return;
            }
            allowedMenuIds.add(pid);
            curId = pid;
        }
    }

    /**
     * 将指定菜单的所有后代加入 allowedMenuIds（允许 root 则允许所有子菜单）
     */
    private void addDescendants(Long menuId, Map<Long, List<Menu>> childrenByParentId, Set<Long> allowedMenuIds) {
        if (menuId == null) return;
        List<Long> stack = new ArrayList<>();
        stack.add(menuId);
        while (!stack.isEmpty()) {
            Long cur = stack.remove(stack.size() - 1);
            if (cur == null) continue;
            allowedMenuIds.add(cur);
            List<Menu> children = childrenByParentId.get(cur);
            if (children == null || children.isEmpty()) continue;
            for (Menu child : children) {
                if (child != null && child.getId() != null) {
                    stack.add(child.getId());
                }
            }
        }
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