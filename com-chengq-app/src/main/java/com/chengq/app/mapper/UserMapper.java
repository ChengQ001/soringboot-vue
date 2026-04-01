package com.chengq.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengq.api.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper（自定义 SQL 见 resources/mapper/UserMapper.xml）
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByUsername(String username);

    User selectByPhone(String phone);

    int countTotalUsers();

    List<User> selectByUsernameLike(String username);

    List<User> selectByIdRange(Long startId, Long endId);
}
