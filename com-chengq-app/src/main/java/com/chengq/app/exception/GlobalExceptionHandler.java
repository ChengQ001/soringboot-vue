package com.chengq.app.exception;

import com.chengq.api.model.base.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理控制器层抛出的异常，返回统一格式的响应
 */
@Slf4j
@RestControllerAdvice // 全局控制器通知，处理所有控制器的异常
public class GlobalExceptionHandler {
    
    /**
     * 处理访问拒绝异常（权限不足）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ApiResponse.error(403, "授权失败");
    }
    
    /**
     * 处理认证不足异常
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ApiResponse<?> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        log.warn("Insufficient authentication: {}", e.getMessage());
        return ApiResponse.error(401, "授权失败");
    }
    
    /**
     * 处理凭证错误异常（用户名或密码错误）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<?> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("Bad credentials: {}", e.getMessage());
        return ApiResponse.error(401, "授权失败");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : err.getField())
                .orElse("参数校验失败");
        log.warn("Validation failed: {}", msg);
        return ApiResponse.error(400, msg);
    }
    
    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGeneralException(Exception e) {
        log.error("General exception: {}", e.getMessage(), e);
        return ApiResponse.error(e.getMessage());
    }
}