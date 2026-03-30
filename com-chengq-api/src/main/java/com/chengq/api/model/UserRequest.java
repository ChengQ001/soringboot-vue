package com.chengq.api.model;

import lombok.Data;

/**
 * 用户请求模型
 */
@Data
public class UserRequest {
    private String username;
    private String password;
    private String role;
}