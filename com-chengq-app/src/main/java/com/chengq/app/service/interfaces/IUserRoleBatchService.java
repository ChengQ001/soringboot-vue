package com.chengq.app.service.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chengq.api.entity.UserRole;

/**
 * 用户-角色关联实体的 MyBatis-Plus 能力封装（如 {@link IService#saveBatch}），
 * 领域规则见 {@link IUserRoleService} / {@link com.chengq.app.service.Impl.UserRoleServiceImpl}。
 */
public interface IUserRoleBatchService extends IService<UserRole> {
}
