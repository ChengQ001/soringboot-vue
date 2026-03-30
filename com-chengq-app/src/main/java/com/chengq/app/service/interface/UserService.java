package com.chengq.app.service.interfaces;

import com.chengq.api.model.DeleteUserRequest;
import com.chengq.api.model.LoginRequest;
import com.chengq.api.model.LoginResponse;
import com.chengq.api.model.RegisterRequest;
import com.chengq.api.model.UpdateUserRequest;
import com.chengq.api.model.UserRequest;
import com.chengq.api.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 用户业务层接口
 * 定义用户相关的业务逻辑方法
 */
public interface UserService extends UserDetailsService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 认证用户
     */
    boolean authenticate(String username, String password);

    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);

    /**
     * 获取所有用户列表
     */
    List<String> getUsers(UserRequest request);

    /**
     * 获取所有用户列表（测试方法）
     */
    List<String> getUsersTest(UserRequest request);

    /**
     * 创建新用户
     */
    String createUser(UserRequest request);

    /**
     * 删除用户
     */
    String deleteUser(DeleteUserRequest request);
    
    /**
     * 用户注册
     */
    String register(RegisterRequest registerRequest);
    
    /**
     * 更新用户信息
     */
    String updateUser(UpdateUserRequest request);
}