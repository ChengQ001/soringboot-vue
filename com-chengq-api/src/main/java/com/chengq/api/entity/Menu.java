package com.chengq.api.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.Data;

/**
 * 菜单实体类
 * 树结构设计
 */
@Data
@TableName("tb_menu")
public class Menu extends BaseEntity {
    
    /**
     * 菜单名称
     */
    private String name;
    
    /**
     * 菜单编码
     */
    private String code;
    
    /**
     * 菜单路径
     */
    private String path;
    
    /**
     * 父节点ID
     */
    @TableField(value = "parent_id", updateStrategy = FieldStrategy.IGNORED)
    private Long parentId;

    /**
     * 排序（越小越靠前）
     */
    @TableField("sort_order")
    private Integer sortOrder;
    
    /**
     * 菜单描述
     */
    private String description;
    
    /**
     * 子菜单列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Menu> children;
}
