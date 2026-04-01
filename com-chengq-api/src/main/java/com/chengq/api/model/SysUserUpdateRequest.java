package com.chengq.api.model;

import lombok.Data;

/**
 * 编辑用户：主键必填，其余可空
 */
@Data
public class SysUserUpdateRequest {
    /** 用户主键（tb_user.id），必填 */
    private Long id;
    /** 新用户名，可空表示不修改 */
    private String username;
    /** 新手机号，可空表示不修改（唯一性由服务端校验） */
    private String phone;
    /** 新密码明文，可空表示不修改；非空时后端会 BCrypt 后写入 */
    private String password;
    /** 新描述，可空表示不修改 */
    private String description;
}
