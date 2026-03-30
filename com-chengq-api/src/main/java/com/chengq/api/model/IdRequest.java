package com.chengq.api.model;

import lombok.Data;

/**
 * 通用主键请求体（配合 POST 查询/删除）
 */
@Data
public class IdRequest {
    private Long id;
}
