package com.chengq.api.model;

import lombok.Data;

/**
 * 新增用户：手机号唯一（业务校验）
 */
@Data
public class SysUserAddRequest {
    private String username;
    private String phone;
    private String password;
    private String description;
}
