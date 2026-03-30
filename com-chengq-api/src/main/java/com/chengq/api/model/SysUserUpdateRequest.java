package com.chengq.api.model;

import lombok.Data;

/**
 * 编辑用户：主键必填，其余可空
 */
@Data
public class SysUserUpdateRequest {
    private Long id;
    private String username;
    private String phone;
    private String password;
    private String description;
}
