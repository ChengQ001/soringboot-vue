package com.chengq.app.service.interfaces;

import java.util.List;

/**
 * 角色菜单关联业务层接口
 * 定义角色菜单关联相关的业务逻辑方法
 */
public interface RoleMenuService {
    
    /**
     * 绑定角色和菜单
     */
    void bindRoleMenus(Long roleId, List<Long> menuIds, Long parkId);
    
    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
    
    /**
     * 根据角色ID删除菜单关联
     */
    void deleteByRoleId(Long roleId);
    
    /**
     * 根据菜单ID删除角色菜单关联
     */
    void deleteByMenuId(Long menuId);
}