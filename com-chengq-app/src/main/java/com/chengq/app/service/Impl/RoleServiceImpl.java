package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chengq.api.entity.Role;
import com.chengq.app.mapper.RoleMapper;
import com.chengq.app.service.interfaces.IRoleService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 角色业务层实现类
 */
@Slf4j
@Service
public class RoleServiceImpl implements IRoleService {

    private final RoleMapper roleMapper;
    private static final String ADMIN_ROLE = "ADMIN";

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    private Role findByName(String name) {
        if (name == null) {
            return null;
        }
        return roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getName, name).last("LIMIT 1"));
    }

    @Override
    public Role createRole(Role role) {
        log.info("Creating role: {}", role.getName());
        Role existingRole = findByName(role.getName());
        if (existingRole != null) {
            throw new RuntimeException("角色名称已存在: " + role.getName());
        }
        roleMapper.insert(role);
        log.info("Role created successfully: {}", role.getName());
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        log.info("Updating role with id: {}", role.getId());
        Role existingRole = roleMapper.selectById(role.getId());
        if (existingRole == null) {
            throw new RuntimeException("角色不存在: " + role.getId());
        }
        if (ADMIN_ROLE.equals(existingRole.getName())) {
            throw new RuntimeException("ADMIN 角色为系统保留角色，禁止修改");
        }
        if (role.getName() != null) {
            Role roleByName = findByName(role.getName());
            if (roleByName != null && !roleByName.getId().equals(role.getId())) {
                throw new RuntimeException("角色名称已存在: " + role.getName());
            }
        }
        roleMapper.updateById(role);
        log.info("Role updated successfully: {}", role.getId());
        return roleMapper.selectById(role.getId());
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在: " + id);
        }
        if (ADMIN_ROLE.equals(role.getName())) {
            throw new RuntimeException("ADMIN 角色为系统保留角色，禁止删除");
        }
        roleMapper.deleteById(id);
        log.info("Role deleted successfully: {}", id);
    }

    @Override
    public Role getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在: " + id);
        }
        return role;
    }

    @Override
    public Role getRoleByName(String name) {
        return findByName(name);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.selectList(null);
    }
}
