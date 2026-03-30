package com.chengq.api.controller;

import com.chengq.api.model.LoginRequest;
import com.chengq.api.model.LoginResponse;
import com.chengq.api.model.RegisterRequest;
import com.chengq.api.model.UpdateUserRequest;
import com.chengq.api.model.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 认证控制器接口，登录接口
 */
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication API")
public interface AuthController {

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    ApiResponse<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest);
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "User Register", description = "Register a new user")
    ApiResponse<String> register(@RequestBody @Validated RegisterRequest registerRequest);

    /**
     * 19. 更新用户
     */
    @PostMapping("/update")
    @Operation(summary = "更新用户", description = "更新用户信息")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<String> updateUser(@RequestBody UpdateUserRequest request);

    /**
     * 退出登录（JWT 无状态：服务端仅清理当前请求上下文；data=true 表示本次退出已受理）
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "需携带有效 JWT；data 为 true 表示成功，客户端仍须删除本地 token")
    ApiResponse<Boolean> logout();
}