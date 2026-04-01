package com.chengq.app.service.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chengq.api.entity.RoleMenu;

/**
 * 角色-菜单关联实体的 MyBatis-Plus 能力封装（如 {@link IService#saveBatch}），
 * 领域规则见 {@link IRoleMenuService} / {@link com.chengq.app.service.Impl.RoleMenuServiceImpl}。
 */
public interface IRoleMenuBatchService extends IService<RoleMenu> {
}
