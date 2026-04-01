package com.chengq.api.model;

import lombok.Data;

/**
 * 更新园区请求体
 */
@Data
public class SysParkUpdateRequest {
    /** 园区主键（tb_park.id） */
    private Long id;
    /** 新名称，可空表示不修改 */
    private String name;
    /** 新备注，可空表示不修改 */
    private String description;
}

