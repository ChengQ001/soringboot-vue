package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chengq.api.entity.User;
import com.chengq.api.entity.UserRole;
import com.chengq.app.mapper.UserMapper;
import com.chengq.app.mapper.UserRoleMapper;
import com.chengq.app.service.interfaces.IUserRoleService;
import com.chengq.app.mapper.UserRoleMpService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserRoleServiceImpl implements IUserRoleService {

    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final UserRoleMpService userRoleMpService;

    public UserRoleServiceImpl(UserRoleMapper userRoleMapper, UserMapper userMapper, UserRoleMpService userRoleMpService) {
        this.userRoleMapper = userRoleMapper;
        this.userMapper = userMapper;
        this.userRoleMpService = userRoleMpService;
    }

    @Override
    @Transactional
    public void bindUserRoles(Long userId, List<Long> roleIds, Long parkId) {
        if (userId == null) {
            throw new RuntimeException("绑定用户角色时 userId 不能为空");
        }
        if (parkId == null) {
            throw new RuntimeException("绑定用户角色时必须选择园区；各园区独立保存，互不影响其他园区已有绑定");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            throw new RuntimeException("绑定用户角色时 roleIds 不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user != null && "admin".equalsIgnoreCase(user.getUsername())) {
            throw new RuntimeException("admin 账号为系统保留账号，禁止在此页面绑定角色");
        }
        log.info("Binding roles to user: {}, parkId: {}", userId, parkId);
        // 仅删除「该用户 + 该园区」下的旧绑定，其它园区的行保持不变
        LambdaQueryWrapper<UserRole> del = new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .eq(UserRole::getParkId, parkId);
        userRoleMapper.delete(del);
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> batch = new ArrayList<>(roleIds.size());
            for (Long roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                ur.setParkId(parkId);
                batch.add(ur);
            }
            userRoleMpService.saveBatch(batch);
        }
        log.info("Roles bound to user successfully: {}", userId);
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId, Long parkId) {
        LambdaQueryWrapper<UserRole> q = new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId);
        if (parkId != null) {
            q.eq(UserRole::getParkId, parkId);
        } else {
            q.isNull(UserRole::getParkId);
        }
        return userRoleMapper.selectList(q).stream()
                .map(UserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUserId(Long userId) {
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId));
    }
}
