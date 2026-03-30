package com.chengq.api.model;

import lombok.Data;

import java.util.Date;

/**
 * 用户管理列表/详情（不含密码）
 */
@Data
public class SysUserVO {
    private Long id;
    private String username;
    private String phone;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}
