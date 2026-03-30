package com.chengq.api.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 * 注册请求，字段与 {@link com.chengq.api.entity.User} 中业务字段一致（用户名、手机号、密码、描述）
 */
@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的11位手机号")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 选填 */
    private String description;
}
