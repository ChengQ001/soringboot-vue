package com.chengq.app.mq;

import com.chengq.app.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ生产者
 * 用于发送消息到RabbitMQ
 */
@Slf4j
@Component
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定队列
     *
     * @param message 消息内容
     */
    public void sendMessage(String message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.DEMO_EXCHANGE,
                    RabbitMQConfig.DEMO_ROUTING_KEY,
                    message
            );
            log.info("消息发送成功: {}", message);
        } catch (Exception e) {
            log.error("消息发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("消息发送失败", e);
        }
    }

    /**
     * 发送对象消息
     *
     * @param object 消息对象
     */
    public void sendObject(Object object) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.DEMO_EXCHANGE,
                    RabbitMQConfig.DEMO_ROUTING_KEY,
                    object
            );
            log.info("对象消息发送成功: {}", object);
        } catch (Exception e) {
            log.error("对象消息发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("对象消息发送失败", e);
        }
    }
}