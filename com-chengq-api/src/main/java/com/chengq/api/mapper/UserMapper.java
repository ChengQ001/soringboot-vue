package com.chengq.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengq.api.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 * 继承BaseMapper以获得MyBatis Plus提供的基础CRUD操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM tb_user WHERE username = #{username} AND deleted = 0 LIMIT 1")
    User selectByUsername(String username);

    /**
     * 根据手机号查询（用于注册去重等）
     */
    @Select("SELECT * FROM tb_user WHERE phone = #{phone} AND deleted = 0 LIMIT 1")
    User selectByPhone(String phone);
    
    /**
     * 统计用户总数
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM tb_user")
    int countTotalUsers();
    
    /**
     * 根据用户名模糊查询
     * @param username 用户名关键字
     * @return 用户列表
     */
    @Select("SELECT id, username, phone, password, description FROM tb_user WHERE username LIKE CONCAT('%', #{username}, '%') ORDER BY id ASC")
    List<User> selectByUsernameLike(String username);
    
    /**
     * 查询指定ID范围的用户
     * @param startId 开始ID
     * @param endId 结束ID
     * @return 用户列表
     */
    @Select("SELECT id, username, phone, password, description FROM tb_user WHERE id BETWEEN #{startId} AND #{endId} ORDER BY id ASC")
    List<User> selectByIdRange(Long startId, Long endId);
}
