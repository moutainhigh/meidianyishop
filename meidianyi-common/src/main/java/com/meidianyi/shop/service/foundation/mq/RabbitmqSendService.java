package com.meidianyi.shop.service.foundation.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * rabbitMQ的失败回调的实现
 * @author: 卢光耀
 * @date: 2019-07-31 14:43
 *
*/
@Slf4j
@Service
public class RabbitmqSendService  {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String CONTENT_TYPE = "application/json";
    private static final String CONTENT_ENCODING = "UTF-8";

    private static final ClassMapper CLASS_MAPPER = new DefaultClassMapper();
    /**
     * 发送消息
     * @param object 发送消息
     * @param queueName 在{@link com.meidianyi.shop.config.mq.RabbitConfig}配置自己的队列
     */
    @Deprecated
    public  void sendMessage(String queueName,Object object){
        log.info("接收队列---{},MQ发送消息---{}",queueName,object);
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(queueName,object,correlationData);
    }
    /**
     * 发送消息
     * @param object 发送消息
     * @param routingKey 路由键名称,在{@link com.meidianyi.shop.config.mq.RabbitConfig}配置
     * @param exchangeName 路由名称,在{@link com.meidianyi.shop.config.mq.RabbitConfig}配置
     */
    public void sendMessage(String exchangeName,String routingKey,Object... object){
        log.info("接收路由---{}，路由键---{}",exchangeName,routingKey);
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(exchangeName,routingKey,object,correlationData);
    }
    /**
     * 发送消息（针对自定义实体）
     * @param routingKey 路由键名称,在{@link com.meidianyi.shop.config.mq.RabbitConfig}配置
     * @param exchangeName 路由名称,在{@link com.meidianyi.shop.config.mq.RabbitConfig}配置
     * @param content 从{@link com.meidianyi.shop.db.main.tables.TaskJobMain}里取出的content值
     * @param className content对应的反序列化的实体类
     */
    public  void sendMessage(String exchangeName,String routingKey,String content,String className)  {
        MessageProperties properties = MessagePropertiesBuilder.newInstance()
            .setContentEncoding(CONTENT_ENCODING)
            .setContentType(CONTENT_TYPE)
            .build();
        if(StringUtils.isNotBlank(className)){
            try {
                Class<?> clz = Class.forName(className);
                CLASS_MAPPER.fromClass(clz,properties);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Message msg = MessageBuilder
            .withBody(content.getBytes())
            .andProperties(properties)
            .build();
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(exchangeName,routingKey,msg,correlationData);
    }
    /**
     * 发送消息（针对通用的实体;类似List等...）
     * @param routingKey 路由键名称,在{@link com.meidianyi.shop.config.mq.RabbitConfig}配置
     * @param exchangeName 路由名称,在{@link com.meidianyi.shop.config.mq.RabbitConfig}配置
     * @param content 从{@link com.meidianyi.shop.db.main.tables.TaskJobMain}里取出的content值
     */
    public  void sendMessage(String exchangeName,String routingKey,String content){
        this.sendMessage(exchangeName,routingKey,content,null);
    }

}
