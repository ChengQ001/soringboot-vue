package com.chengq.api.model;

import lombok.Data;

/**
 * 新增用户：手机号唯一（业务校验）
 */
@Data
public class SysUserAddRequest {
    /** 登录名/展示名 */
    private String username;
    /** 手机号，全局唯一（业务校验） */
    private String phone;
    /** 初始密码明文，后端 BCrypt 入库 */
    private String password;
    /** 备注，可空 */
    private String description;
}
