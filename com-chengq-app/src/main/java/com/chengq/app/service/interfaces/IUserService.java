package com.chengq.app.service.interfaces;

import com.chengq.api.entity.User;
import com.chengq.api.model.DeleteUserRequest;
import com.chengq.api.model.LoginRequest;
import com.chengq.api.model.LoginResponse;
import com.chengq.api.model.RegisterRequest;
import com.chengq.api.model.UpdateUserRequest;
import com.chengq.api.model.UserRequest;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户业务层接口
 */
public interface IUserService extends UserDetailsService {

    LoginResponse login(LoginRequest loginRequest);

    boolean authenticate(String username, String password);

    User getUserByUsername(String username);

    List<String> getUsers(UserRequest request);

    List<String> getUsersTest(UserRequest request);

    String createUser(UserRequest request);

    String deleteUser(DeleteUserRequest request);

    String register(RegisterRequest registerRequest);

    String updateUser(UpdateUserRequest request);
}
