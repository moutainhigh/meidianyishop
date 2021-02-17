package com.meidianyi.shop.service.foundation.mq;


import org.apache.commons.lang3.ArrayUtils;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * mq工具
 * @author luguangyao
 */
@Component
public class RabbitMqUtilService {

    @Autowired
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;


    /**
     * 暂停指定队列消费
     * @param queueName 队列名
     * @return true:成功|false:失败
     */
    public boolean stopQueueConsumption(String queueName){
        Collection<MessageListenerContainer>  listenerContainers =
            rabbitListenerEndpointRegistry.getListenerContainers();
        for ( MessageListenerContainer container: listenerContainers ) {
            if( isQueueListener(queueName,container) ){
                container.stop();
                return true;
            }
        }
        return false;
    }

    /**
     * 重新启用所有监听消费
     */
    public void startQueueConsumption(){
        rabbitListenerEndpointRegistry.start();
    }
    /**
     * 判断当前容器是否监听指定队列
     * @param queueName 队列名称
     * @param container 监听容器
     * @return true:监听｜false:未监听
     */
    private boolean isQueueListener(String queueName,MessageListenerContainer container){
        if ( container instanceof AbstractMessageListenerContainer ){
            AbstractMessageListenerContainer listenerContainer = (AbstractMessageListenerContainer)container;
            String[] queueNames = listenerContainer.getQueueNames();
            return ArrayUtils.contains(queueNames,queueName);
        }
        return false;
    }
}
