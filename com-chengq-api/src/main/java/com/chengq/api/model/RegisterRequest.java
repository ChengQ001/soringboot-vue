package com.chengq.api.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 * 注册请求，字段与 {@link com.chengq.api.entity.User} 中业务字段一致（用户名、手机号、密码、描述）
 */
@Data
public class RegisterRequest {
    /** 用户名，唯一性由服务端校验 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 11 位大陆手机号 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的11位手机号")
    private String phone;

    /** 明文密码，后端 BCrypt 后写入 tb_user.password */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 个人描述，选填 */
    private String description;
}
