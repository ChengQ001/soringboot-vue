package com.chengq.app.service;

import com.chengq.api.model.HelloRequest;

/**
 * Hello业务层接口
 * 定义Hello相关的业务逻辑方法
 */
public interface HelloService {

    /**
     * 返回Hello消息
     */
    String hello(HelloRequest request);
}