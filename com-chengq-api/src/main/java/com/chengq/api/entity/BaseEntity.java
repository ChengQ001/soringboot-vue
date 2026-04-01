package com.chengq.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.util.Date;
import lombok.Data;

/**
 * 基础实体类
 * 包含通用字段：创建时间、创建人、更新时间、更新人、版本号、是否删除等
 */
@Data
public class BaseEntity {
    
    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;
    
    /**
     * 创建人ID
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;


    /**
     * 创建人名称
     */
    @TableField(value = "created_by_name", fill = FieldFill.INSERT)
    private String createdByName;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
    
    /**
     * 更新人ID
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;
    
    /**
     * 更新人名称
     */
    @TableField(value = "updated_by_name", fill = FieldFill.INSERT_UPDATE)
    private String updatedByName;
    
    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;
    
    /**
     * 是否删除（逻辑删除）
     */
    @TableLogic
    private Integer deleted;
}
