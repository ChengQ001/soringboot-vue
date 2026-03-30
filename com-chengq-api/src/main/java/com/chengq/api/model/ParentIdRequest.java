package com.chengq.api.model;

import lombok.Data;

/**
 * 父级 ID 请求体（菜单子节点等）
 */
@Data
public class ParentIdRequest {
    private Long parentId;
}
