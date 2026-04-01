package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chengq.api.entity.UserRole;
import com.chengq.app.mapper.UserRoleMapper;
import com.chengq.app.service.interfaces.IUserRoleBatchService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleBatchServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleBatchService {
}
