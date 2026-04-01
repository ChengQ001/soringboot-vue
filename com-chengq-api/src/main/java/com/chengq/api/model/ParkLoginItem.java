package com.chengq.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录返回：用户可访问的园区（用于前端下拉切换）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkLoginItem {
    /** 园区主键（tb_park.id），与请求头 X-Park-Id 取值一致 */
    private Long id;
    /** 园区名称（展示用） */
    private String name;
}
