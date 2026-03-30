package com.chengq.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色实体类
 */
@Data
@TableName("tb_role")
public class Role extends BaseEntity {
    
    /**
     * 角色名称（唯一）
     */
    private String name;
    
    /**
     * 角色描述
     */
    private String description;
}