package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chengq.api.entity.Menu;
import com.chengq.api.entity.Role;
import com.chengq.api.entity.RoleMenu;
import com.chengq.api.entity.UserRole;
import com.chengq.app.mapper.MenuMapper;
import com.chengq.app.mapper.RoleMapper;
import com.chengq.app.mapper.RoleMenuMapper;
import com.chengq.app.exception.BizCodes;
import com.chengq.app.exception.BizException;
import com.chengq.app.mapper.UserRoleMapper;
import com.chengq.app.service.interfaces.IMenuService;
import com.chengq.app.util.ParkContext;
import com.chengq.app.util.UserContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 菜单业务层实现类
 * 实现菜单相关的业务逻辑，使用MyBatis Plus进行数据库操作
 */
@Slf4j
@Service
public class MenuServiceImpl implements IMenuService {

    private final MenuMapper menuMapper;
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    public MenuServiceImpl(MenuMapper menuMapper, RoleMapper roleMapper, RoleMenuMapper roleMenuMapper,
                           UserRoleMapper userRoleMapper) {
        this.menuMapper = menuMapper;
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.userRoleMapper = userRoleMapper;
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
            throw new BizException(BizCodes.INTERNAL_ERROR, "新增菜单失败：" + (e.getMessage() != null ? e.getMessage() : "未知原因"), e);
        }
    }

    @Override
    public Menu updateMenu(Menu menu) {
        try {
            log.info("Updating menu with id: {}", menu.getId());

            Menu existingMenu = menuMapper.selectById(menu.getId());
            if (existingMenu == null) {
                throw new BizException(BizCodes.NOT_FOUND, "未找到菜单，编号：" + menu.getId());
            }

            applyTwoLevelAndSort(menu, existingMenu);
            menuMapper.updateById(menu);
            log.info("Menu updated successfully: {}", menu.getId());
            return menu;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update menu: {}", e.getMessage());
            throw new BizException(BizCodes.INTERNAL_ERROR, "更新菜单失败：" + (e.getMessage() != null ? e.getMessage() : "未知原因"), e);
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
                throw new BizException(BizCodes.BAD_REQUEST, "该菜单下已有子节点，不能改为子菜单（仅支持两级）");
            }
        }

        if (pid != null) {
            if (existing != null && pid.equals(menu.getId())) {
                throw new BizException(BizCodes.BAD_REQUEST, "父菜单不能为自身");
            }
            Menu parent = menuMapper.selectById(pid);
            if (parent == null) {
                throw new BizException(BizCodes.NOT_FOUND, "父菜单不存在");
            }
            if (!isRootMenu(parent)) {
                throw new BizException(BizCodes.BAD_REQUEST, "仅支持两级菜单：子菜单的父级必须是一级菜单");
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
                throw new BizException(BizCodes.NOT_FOUND, "未找到菜单，编号：" + id);
            }
            
            // 检查是否有子菜单
            List<Menu> children = menuMapper.selectChildrenByParentId(id);
            if (!children.isEmpty()) {
                throw new BizException(BizCodes.BAD_REQUEST, "该菜单下存在子菜单，请先删除子菜单");
            }
            
            menuMapper.deleteById(id);
            log.info("Menu deleted successfully: {}", id);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete menu: {}", e.getMessage());
            throw new BizException(BizCodes.INTERNAL_ERROR, "删除菜单失败：" + (e.getMessage() != null ? e.getMessage() : "未知原因"), e);
        }
    }

    @Override
    public Menu getMenuById(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BizException(BizCodes.NOT_FOUND, "未找到菜单，编号：" + id);
        }
        return menu;
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuMapper.selectAllMenus();
    }

    @Override
    public List<Menu> getMenuTree() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        // ADMIN：不依赖 tb_role_menu，直接全量菜单（与园区无关，避免未带 X-Park-Id 时侧栏为空）
        if (UserContext.hasRole("ADMIN")) {
            List<Menu> allMenus = menuMapper.selectAllMenus();
            return buildMenuTree(allMenus);
        }

        Long parkId = ParkContext.getParkId();
        if (parkId == null) {
            return new ArrayList<>();
        }

        // 非 ADMIN：按当前园区（X-Park-Id）+ 用户角色 + tb_role_menu（本园区优先于全局）建树
        // 用户角色：当前园区显式绑定 + park_id 为 NULL 的全局绑定（分两路查询，避免 AND/OR 组合歧义）
        List<UserRole> userRolesForPark = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
                        .eq(UserRole::getParkId, parkId)
        );
        List<UserRole> userRolesGlobal = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
                        .isNull(UserRole::getParkId)
        );
        List<UserRole> userRoles = new ArrayList<>();
        if (userRolesForPark != null) {
            userRoles.addAll(userRolesForPark);
        }
        if (userRolesGlobal != null) {
            userRoles.addAll(userRolesGlobal);
        }
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 角色菜单：当前园区 + 全局 NULL，分两路再合并
        List<RoleMenu> roleMenusForPark = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .in(RoleMenu::getRoleId, roleIds)
                        .eq(RoleMenu::getParkId, parkId)
        );
        List<RoleMenu> roleMenusGlobal = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .in(RoleMenu::getRoleId, roleIds)
                        .isNull(RoleMenu::getParkId)
        );

        // 按角色合并：该角色在本园区「有任意一行」绑定则只采用本园区行，不再叠加 park_id 为 NULL 的全局行，
        // 避免在园区里只勾了父节点却因全局历史数据仍带上全部子菜单。
        Set<Long> mappedMenuIds = new HashSet<>();
        for (Long rid : roleIds) {
            List<RoleMenu> parkRows = new ArrayList<>();
            if (roleMenusForPark != null) {
                for (RoleMenu rm : roleMenusForPark) {
                    if (rm != null && rid.equals(rm.getRoleId())) {
                        parkRows.add(rm);
                    }
                }
            }
            if (!parkRows.isEmpty()) {
                for (RoleMenu rm : parkRows) {
                    if (rm.getMenuId() != null) {
                        mappedMenuIds.add(rm.getMenuId());
                    }
                }
            } else if (roleMenusGlobal != null) {
                for (RoleMenu rm : roleMenusGlobal) {
                    if (rm != null && rid.equals(rm.getRoleId()) && rm.getMenuId() != null) {
                        mappedMenuIds.add(rm.getMenuId());
                    }
                }
            }
        }

        if (mappedMenuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3) 构建 id -> 菜单（用于向上解析祖先）
        List<Menu> allMenus = menuMapper.selectAllMenus();
        Map<Long, Menu> byId = new HashMap<>();
        for (Menu m : allMenus) {
            if (m == null || m.getId() == null) continue;
            byId.put(m.getId(), m);
        }

        // 4) 允许集合：仅显式绑定的菜单 + 其祖先（用于侧栏层级）；不自动包含未绑定的子节点
        Set<Long> allowedMenuIds = new HashSet<>();
        for (Long mappedId : mappedMenuIds) {
            allowedMenuIds.add(mappedId);
            addAncestors(mappedId, byId, allowedMenuIds);
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
