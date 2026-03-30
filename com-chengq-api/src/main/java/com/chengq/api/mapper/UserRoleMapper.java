package com.chengq.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengq.api.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色映射 Mapper（自定义条件使用 {@link com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper}）
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
