package com.chengq.app.service.Impl;

import com.chengq.api.model.HelloRequest;
import com.chengq.app.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Hello业务层实现类
 * 实现Hello相关的业务逻辑
 */
@Slf4j
@Service
public class HelloServiceImpl implements HelloService {

    /**
     * 返回Hello消息
     */
    @Override
    public String hello(HelloRequest request) {
        log.info("HelloService hello() method is called with message: {}", request.getMessage());
        log.debug("Debug information for hello()");
        log.warn("Warning message from hello()");
        return "Hello, Spring Boot!";
    }
}