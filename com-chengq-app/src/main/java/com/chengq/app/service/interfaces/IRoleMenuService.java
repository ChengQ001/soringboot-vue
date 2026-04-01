package com.chengq.app.service.interfaces;

import java.util.List;

/**
 * 角色菜单关联业务层接口
 */
public interface IRoleMenuService {

    void bindRoleMenus(Long roleId, List<Long> menuIds, Long parkId);

    List<Long> getMenuIdsByRoleId(Long roleId, Long parkId);

    void deleteByRoleId(Long roleId);

    void deleteByMenuId(Long menuId);
}
