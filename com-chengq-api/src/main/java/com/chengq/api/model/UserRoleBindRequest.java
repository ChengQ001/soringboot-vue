package com.chengq.api.model;

import java.util.List;
import lombok.Data;

/**
 * 用户-角色绑定请求体（POST /permissions/user-roles），写入 tb_user_role。
 */
@Data
public class UserRoleBindRequest {
    /** 用户主键（tb_user.id） */
    private Long userId;
    /** 角色主键列表（tb_role.id） */
    private List<Long> roleIds;
    /** 园区主键（tb_park.id），与绑定记录 park_id 一致 */
    private Long parkId;
}
