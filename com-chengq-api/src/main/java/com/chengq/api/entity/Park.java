package com.chengq.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 园区实体
 */
@Data
@TableName("tb_park")
public class Park extends BaseEntity {

    /**
     * 园区名称（唯一）
     */
    private String name;

    /**
     * 园区描述
     */
    private String description;
}

