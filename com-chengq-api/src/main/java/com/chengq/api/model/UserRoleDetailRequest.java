package com.chengq.api.model;

import lombok.Data;

/**
 * 查询用户在某园区下已绑定的角色：id 为用户主键；parkId 为空时只查 park_id 为 NULL 的历史行
 */
@Data
public class UserRoleDetailRequest {
    /** 用户主键（tb_user.id），必填 */
    private Long id;
    /**
     * 园区主键（tb_park.id）。为 {@code null} 时仅查询 {@code park_id IS NULL} 的历史绑定行；
     * 指定时查询该园区下的用户-角色绑定。
     */
    private Long parkId;
}
