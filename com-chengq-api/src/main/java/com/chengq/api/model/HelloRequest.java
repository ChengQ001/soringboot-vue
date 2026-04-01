package com.chengq.api.model;

import lombok.Data;

/**
 * 健康检查/示例接口请求体
 */
@Data
public class HelloRequest {
    /** 任意问候或测试字符串 */
    private String message;
}