package com.chengq.app.service.interfaces;

import com.chengq.api.entity.Role;
import java.util.List;

/**
 * 角色业务层接口
 */
public interface IRoleService {

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Long id);

    Role getRoleById(Long id);

    Role getRoleByName(String name);

    List<Role> getAllRoles();
}
