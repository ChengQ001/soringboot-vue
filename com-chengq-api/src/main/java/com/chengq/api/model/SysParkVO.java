package com.chengq.api.model;

import java.util.Date;
import lombok.Data;

/**
 * 园区列表/详情展示（不含敏感字段）
 */
@Data
public class SysParkVO {
    /** 园区主键 */
    private Long id;
    /** 园区名称 */
    private String name;
    /** 备注 */
    private String description;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}

