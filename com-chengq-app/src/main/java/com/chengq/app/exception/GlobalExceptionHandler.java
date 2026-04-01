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

    @ExceptionHandler(BizException.class)
    public ApiResponse<?> handleBizException(BizException e) {
        log.warn("业务异常 [{}] {}", e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 处理访问拒绝异常（权限不足）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("拒绝访问: {}", e.getMessage());
        return ApiResponse.error(403, "没有权限执行此操作");
    }
    
    /**
     * 处理认证不足异常
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ApiResponse<?> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        log.warn("认证不足: {}", e.getMessage());
        return ApiResponse.error(401, "未登录或登录已失效");
    }
    
    /**
     * 处理凭证错误异常（用户名或密码错误）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<?> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("凭证错误: {}", e.getMessage());
        return ApiResponse.error(401, "手机号或密码错误");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : err.getField())
                .orElse("参数校验失败");
        log.warn("参数校验失败: {}", msg);
        return ApiResponse.error(400, msg);
    }

    /**
     * 未使用 {@link BizException} 的 {@link RuntimeException}（框架或遗留代码），默认按 400 返回文案
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleBizRuntimeException(RuntimeException e) {
        log.warn("运行时异常: {}", e.getMessage(), e);
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = "操作失败";
        }
        return ApiResponse.error(BizCodes.BAD_REQUEST, msg);
    }

    /**
     * 其它未被 {@link RuntimeException} 覆盖的异常，返回统一中文提示（详细原因见日志）
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGeneralException(Exception e) {
        log.error("未预期异常: {}", e.getMessage(), e);
        return ApiResponse.error("服务器异常，请稍后再试");
    }
}
