package com.chengq.app.mapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chengq.api.entity.RoleMenu;
import org.springframework.stereotype.Service;

/**
 * 仅用于 {@link ServiceImpl#saveBatch} 等 MP 批量能力，业务逻辑仍在 {@link com.chengq.app.service.interfaces.IRoleMenuService} 实现类中。
 */
@Service
public class RoleMenuMpService extends ServiceImpl<RoleMenuMapper, RoleMenu> {
}
