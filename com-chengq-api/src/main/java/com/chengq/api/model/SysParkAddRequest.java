package com.chengq.api.model;

import lombok.Data;

/**
 * 新增园区请求体
 */
@Data
public class SysParkAddRequest {
    /** 园区名称，业务内唯一性由服务端校验 */
    private String name;
    /** 备注说明，可空 */
    private String description;
}

