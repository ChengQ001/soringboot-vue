package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chengq.api.entity.RoleMenu;
import com.chengq.app.mapper.RoleMenuMapper;
import com.chengq.app.service.interfaces.IRoleMenuBatchService;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuBatchServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuBatchService {
}
