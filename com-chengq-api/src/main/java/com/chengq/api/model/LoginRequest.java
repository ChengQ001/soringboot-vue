package com.chengq.api.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    /**
     * 登录账号：手机号
     * 注意：这里字段名为 phone，但接口语义上用于“手机号登录”
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 登录密码：明文（后端会与 tb_user.password 的 BCrypt 密文进行 matches）
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}