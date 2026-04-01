package com.chengq.api.model;

import lombok.Data;

/**
 * 简易用户请求（演示/兼容接口），字段含义依具体 Controller 而定。
 */
@Data
public class UserRequest {
    /** 用户名 */
    private String username;
    /** 密码明文 */
    private String password;
    /** 角色编码或名称（如 USER、ADMIN） */
    private String role;
}