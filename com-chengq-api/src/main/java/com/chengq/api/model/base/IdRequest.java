package com.chengq.api.model.base;

import lombok.Data;

/**
 * 通用主键请求体（配合 POST 查询/删除）
 */
@Data
public class IdRequest {
    /** 业务主键（含义依接口而定：用户/角色/菜单等） */
    private Long id;
}
