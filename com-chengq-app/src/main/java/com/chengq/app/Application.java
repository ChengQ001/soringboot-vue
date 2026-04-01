package com.chengq.app;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.chengq.app", "com.chengq.api"})
@MapperScan(basePackages = {"com.chengq.app.mapper"})
@Slf4j
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("================ start app success =======================");
        log.info("================ Swagger:http://localhost:8080/swagger ======================" );
        log.info("================ API:http://localhost:8080/api-docs ======================");
    }
}