package com.chengq.app.implcontroller;

import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.exception.BizException;
import com.chengq.app.exception.BizCodes;
import com.chengq.app.mq.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RabbitMQ测试控制器
 * 提供发送消息的接口
 */
@RestController
@RequestMapping("/mq")
public class RabbitMQController {

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    /**
     * 发送文本消息
     *
     * @param message 消息内容
     * @return 响应结果
     */
    @PostMapping("/send")
    public ApiResponse<String> sendMessage(@RequestBody String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new BizException(BizCodes.BAD_REQUEST, "消息内容不能为空");
        }
        rabbitMQProducer.sendMessage(message);
        return ApiResponse.success("消息发送成功");
    }

    /**
     * 发送对象消息
     *
     * @param object 消息对象
     * @return 响应结果
     */
    @PostMapping("/send-object")
    public ApiResponse<String> sendObject(@RequestBody Object object) {
        if (object == null) {
            throw new BizException(BizCodes.BAD_REQUEST, "消息对象不能为空");
        }
        rabbitMQProducer.sendObject(object);
        return ApiResponse.success("对象消息发送成功");
    }
}