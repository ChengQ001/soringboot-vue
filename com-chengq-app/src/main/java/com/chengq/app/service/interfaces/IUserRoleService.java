package com.chengq.app.service.interfaces;

import java.util.List;

/**
 * 用户角色关联业务层接口
 */
public interface IUserRoleService {

    void bindUserRoles(Long userId, List<Long> roleIds, Long parkId);

    List<Long> getRoleIdsByUserId(Long userId, Long parkId);

    void deleteByUserId(Long userId);

    void deleteByRoleId(Long roleId);
}
