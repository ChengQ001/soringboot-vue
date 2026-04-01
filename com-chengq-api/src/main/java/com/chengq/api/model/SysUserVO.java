package com.chengq.api.model;

import java.util.Date;
import lombok.Data;

/**
 * 用户管理列表/详情（不含密码）
 */
@Data
public class SysUserVO {
    /** 用户主键 */
    private Long id;
    /** 用户名 */
    private String username;
    /** 手机号 */
    private String phone;
    /** 描述 */
    private String description;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
