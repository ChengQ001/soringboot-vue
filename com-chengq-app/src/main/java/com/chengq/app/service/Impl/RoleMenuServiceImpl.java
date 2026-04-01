package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chengq.api.entity.Menu;
import com.chengq.api.entity.Role;
import com.chengq.api.entity.RoleMenu;
import com.chengq.app.mapper.MenuMapper;
import com.chengq.app.mapper.RoleMapper;
import com.chengq.app.mapper.RoleMenuMapper;
import com.chengq.app.service.interfaces.IRoleMenuService;
import com.chengq.app.mapper.RoleMenuMpService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RoleMenuServiceImpl implements IRoleMenuService {

    private final RoleMenuMapper roleMenuMapper;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final RoleMenuMpService roleMenuMpService;

    public RoleMenuServiceImpl(
            RoleMenuMapper roleMenuMapper,
            RoleMapper roleMapper,
            MenuMapper menuMapper,
            RoleMenuMpService roleMenuMpService) {
        this.roleMenuMapper = roleMenuMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.roleMenuMpService = roleMenuMpService;
    }

    private boolean isAdminRole(Long roleId) {
        if (roleId == null) {
            return false;
        }
        Role r = roleMapper.selectById(roleId);
        return r != null && "ADMIN".equals(r.getName());
    }

    @Override
    @Transactional
    public void bindRoleMenus(Long roleId, List<Long> menuIds, Long parkId) {
        if (roleId == null) {
            throw new RuntimeException("绑定角色菜单时 roleId 不能为空");
        }
        if (parkId == null) {
            throw new RuntimeException("绑定角色菜单时必须选择园区；各园区独立保存，互不影响其它园区");
        }
        if (menuIds == null || menuIds.isEmpty()) {
            throw new RuntimeException("绑定角色菜单时 menuIds 不能为空");
        }
        if (isAdminRole(roleId)) {
            throw new RuntimeException("ADMIN 角色拥有全部菜单，无需且不允许通过角色-菜单绑定修改");
        }
        log.info("Binding menus to role: {}, parkId: {}", roleId, parkId);
        // 仅删除「该角色 + 该园区」下的旧绑定，其它园区的 tb_role_menu 行保留
        LambdaQueryWrapper<RoleMenu> del = new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId)
                .eq(RoleMenu::getParkId, parkId);
        roleMenuMapper.delete(del);
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> batch = new ArrayList<>(menuIds.size());
            for (Long menuId : menuIds) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                rm.setParkId(parkId);
                batch.add(rm);
            }
            roleMenuMpService.saveBatch(batch);
        }
        log.info("Menus bound to role successfully: {}, parkId: {}", roleId, parkId);
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId, Long parkId) {
        if (parkId == null) {
            throw new RuntimeException("查询角色菜单绑定需指定园区");
        }
        if (isAdminRole(roleId)) {
            List<Long> all = new ArrayList<>();
            for (Menu m : menuMapper.selectAllMenus()) {
                if (m != null && m.getId() != null) {
                    all.add(m.getId());
                }
            }
            return all;
        }
        Set<Long> ids = new LinkedHashSet<>();
        List<RoleMenu> forPark = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleId)
                        .eq(RoleMenu::getParkId, parkId)
        );
        List<RoleMenu> globalRows = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleId)
                        .isNull(RoleMenu::getParkId)
        );
        if (forPark != null && !forPark.isEmpty()) {
            for (RoleMenu rm : forPark) {
                if (rm != null && rm.getMenuId() != null) {
                    ids.add(rm.getMenuId());
                }
            }
        } else {
            for (RoleMenu rm : globalRows) {
                if (rm != null && rm.getMenuId() != null) {
                    ids.add(rm.getMenuId());
                }
            }
        }
        return new ArrayList<>(ids);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
    }

    @Override
    public void deleteByMenuId(Long menuId) {
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, menuId));
    }
}
