package com.chengq.api.model;

import lombok.Data;

/**
 * 父级 ID 请求体（菜单子节点等）
 */
@Data
public class ParentIdRequest {
    /** 父节点主键（如菜单 parent_id）；根节点可为 0 或 null，依接口约定 */
    private Long parentId;
}
