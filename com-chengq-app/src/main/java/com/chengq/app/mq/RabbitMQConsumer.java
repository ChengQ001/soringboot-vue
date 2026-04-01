package com.chengq.app.mq;

import com.chengq.app.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ消费者
 * 用于监听并处理RabbitMQ消息
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    /**
     * 监听消息队列，处理接收到的消息
     *
     * @param message 接收到的消息
     */
    @RabbitListener(queues = RabbitMQConfig.DEMO_QUEUE)
    public void receiveMessage(String message) {
        log.info("收到消息: {}", message);
        try {
            // 在这里处理消息
            processMessage(message);
            log.info("消息处理成功");
        } catch (Exception e) {
            log.error("消息处理失败: {}", e.getMessage(), e);
            // 可以根据业务需求决定是否抛出异常（触发重试机制）
            // throw e;
        }
    }

    /**
     * 处理消息的具体业务逻辑
     *
     * @param message 消息内容
     */
    private void processMessage(String message) {
        // 示例：打印消息内容
        log.info("处理消息内容: {}", message);
        
        // 实际业务场景中，这里可以进行：
        // 1. 数据持久化
        // 2. 调用其他服务
        // 3. 发送通知
        // 4. 执行定时任务等
    }

    /**
     * 监听对象消息
     *
     * @param object 接收到的对象消息
     */
    @RabbitListener(queues = RabbitMQConfig.DEMO_QUEUE)
    public void receiveObject(Object object) {
        log.info("收到对象消息: {}", object);
        try {
            // 处理对象消息
            processObject(object);
            log.info("对象消息处理成功");
        } catch (Exception e) {
            log.error("对象消息处理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理对象消息的具体业务逻辑
     *
     * @param object 对象消息
     */
    private void processObject(Object object) {
        log.info("处理对象消息: {}", object);
        // 实际业务逻辑处理
    }
}