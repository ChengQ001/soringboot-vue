package com.chengq.api.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应包装：HTTP 仍可为 200，业务成功与否看 {@link #code} 与 {@link #msg}。
 *
 * @param <T> 业务数据类型，无体时为 {@code null}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /** 业务状态码，约定 200 表示成功 */
    private int code;
    /** 提示信息，失败时承载错误原因 */
    private String msg;
    /** 业务载荷 */
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }
    
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }
    
    public static <T> ApiResponse<T> error(int code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }
    
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(500, msg, null);
    }
}