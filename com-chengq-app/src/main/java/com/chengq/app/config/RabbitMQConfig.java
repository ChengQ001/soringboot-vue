package com.chengq.app.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 配置队列、交换机和绑定关系
 */
@Configuration
public class RabbitMQConfig {

    // 队列名称
    public static final String DEMO_QUEUE = "demo.queue";
    
    // 交换机名称
    public static final String DEMO_EXCHANGE = "demo.exchange";
    
    // 路由键
    public static final String DEMO_ROUTING_KEY = "demo.routing.key";

    /**
     * 创建队列
     */
    @Bean
    public Queue demoQueue() {
        // durable: 是否持久化
        // exclusive: 是否排他（仅创建者可以访问）
        // autoDelete: 当最后一个消费者断开连接时是否自动删除队列
        // arguments: 队列参数
        return new Queue(DEMO_QUEUE, true, false, false);
    }

    /**
     * 创建交换机
     */
    @Bean
    public DirectExchange demoExchange() {
        // durable: 是否持久化
        // autoDelete: 当没有队列绑定时是否自动删除
        return new DirectExchange(DEMO_EXCHANGE, true, false);
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding demoBinding(Queue demoQueue, DirectExchange demoExchange) {
        return BindingBuilder.bind(demoQueue).to(demoExchange).with(DEMO_ROUTING_KEY);
    }
}