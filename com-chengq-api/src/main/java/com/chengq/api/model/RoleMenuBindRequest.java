package com.chengq.api.model;

import java.util.List;
import lombok.Data;

/**
 * 角色-菜单绑定请求体（POST /permissions/role-menus），写入 tb_role_menu。
 */
@Data
public class RoleMenuBindRequest {
    /** 角色主键（tb_role.id）；非 ADMIN 角色 */
    private Long roleId;
    /** 菜单主键列表（tb_menu.id） */
    private List<Long> menuIds;
    /** 园区主键（tb_park.id），与绑定记录 park_id 一致 */
    private Long parkId;
}
