package com.chengq.api.model;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新用户请求（开放注册/演示场景）
 */
@Data
public class UpdateUserRequest {
    /** 用户主键（tb_user.id） */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /** 新用户名，可空表示不修改 */
    private String username;

    /** 新密码明文，可空表示不修改 */
    private String password;

    /** 角色，可空表示不修改 */
    private String role;
}
