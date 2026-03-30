package com.chengq.app.service.interfaces;

import java.util.List;

/**
 * 用户角色关联业务层接口
 * 定义用户角色关联相关的业务逻辑方法
 */
public interface UserRoleService {
    
    /**
     * 绑定用户和角色
     */
    void bindUserRoles(Long userId, List<Long> roleIds, Long parkId);
    
    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);
    
    /**
     * 根据用户ID删除角色关联
     */
    void deleteByUserId(Long userId);
    
    /**
     * 根据角色ID删除用户角色关联
     */
    void deleteByRoleId(Long roleId);
}