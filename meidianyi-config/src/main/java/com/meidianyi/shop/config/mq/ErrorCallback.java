package com.meidianyi.shop.config.mq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

/**
 * 发送失败回调
 * @author 卢光耀
 * @date 2019-08-05 14:40
 *
*/
@Slf4j
@Service
public class ErrorCallback implements RabbitTemplate.ReturnCallback {

    private final RabbitTemplate rabbitTemplate;
    @Lazy
    public ErrorCallback(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    /**
     * 具体实现
     * @param message   发送的消息
     * @param replyCode 错误状态码
     * @param replyText 错误原因
     * @param exchange  交换机名字
     * @param routingKey    路由键
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText,
                                String exchange, String routingKey) {
        log.error("发送队列消息失败，发送到回调队列...");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.set("msg",mapper.valueToTree(message));
        data.set("content",mapper.valueToTree(new String(message.getBody())));
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_ERROR_SEND,data);
        log.error("发送到回调队列成功...");
    }
}
