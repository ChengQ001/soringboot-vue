package com.chengq.app.service.interfaces;

import com.chengq.api.entity.Role;

import java.util.List;

/**
 * 角色业务层接口
 * 定义角色相关的业务逻辑方法
 */
public interface RoleService {
    
    /**
     * 创建新角色
     */
    Role createRole(Role role);
    
    /**
     * 更新角色信息
     */
    Role updateRole(Role role);
    
    /**
     * 删除角色
     */
    void deleteRole(Long id);
    
    /**
     * 根据ID查询角色
     */
    Role getRoleById(Long id);
    
    /**
     * 根据名称查询角色
     */
    Role getRoleByName(String name);
    
    /**
     * 查询所有角色
     */
    List<Role> getAllRoles();
}