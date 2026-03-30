package com.chengq.app.implcontroller;

import com.chengq.api.controller.AuthController;
import com.chengq.api.model.LoginRequest;
import com.chengq.api.model.LoginResponse;
import com.chengq.api.model.RegisterRequest;
import com.chengq.api.model.UpdateUserRequest;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthControllerImpl implements AuthController {
    
    @Autowired
    private UserService userService;
    
    @Override
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        return ApiResponse.success(userService.login(loginRequest));
    }
    
    @Override
    public ApiResponse<String> register(RegisterRequest registerRequest) {
        return ApiResponse.success(userService.register(registerRequest));
    }

    @Override
    public ApiResponse<String> updateUser(@RequestBody UpdateUserRequest request) {
        return ApiResponse.success(userService.updateUser(request));
    }

    @Override
    public ApiResponse<Boolean> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            log.info("User logout: {}", auth.getName());
        }
        SecurityContextHolder.clearContext();
        return ApiResponse.success(Boolean.TRUE);
    }
}