package com.chengq.app.service.interfaces;

import com.chengq.api.entity.User;
import com.chengq.api.model.LoginRequest;
import com.chengq.api.model.LoginResponse;
import com.chengq.api.model.RegisterRequest;
import com.chengq.api.model.UpdateUserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户业务层接口（认证、注册、资料更新等；演示用批量 CRUD 已移除）
 */
public interface IUserService extends UserDetailsService {

    LoginResponse login(LoginRequest loginRequest);

    User getUserByUsername(String username);

    String register(RegisterRequest registerRequest);

    String updateUser(UpdateUserRequest request);
}
