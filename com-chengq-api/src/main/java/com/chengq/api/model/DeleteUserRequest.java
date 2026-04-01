package com.chengq.api.model;

import lombok.Data;

/**
 * 按主键删除用户
 */
@Data
public class DeleteUserRequest {
    /** 待删除用户主键（tb_user.id） */
    private Long id;
}