package com.meidianyi.shop.mq.aspect;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.saas.SaasApplication;

/**
 *
 * @author 卢光耀
 * @date 2019-08-19 15:03
 *
*/
@Aspect
@Component
public class BaseListenerAspect {


    @Pointcut("@annotation(org.springframework.amqp.rabbit.annotation.RabbitHandler)")
    public void mqSuccessListenerAspect(){}


    @Around(value = "mqSuccessListenerAspect()")
    public void successAspect(ProceedingJoinPoint point) throws IOException{
        BaseRabbitHandler handler = (BaseRabbitHandler)point.getThis();
        Object[] dataArray = point.getArgs().clone();
        int len = dataArray.length;
        Object param = dataArray[0];
        Message msg = (Message)dataArray[len-2];
        Channel channel = (Channel)dataArray[len-1];
        try {
            point.proceed();
            Integer id = assertTaskJobId(param);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (Throwable throwable) {
            handler.executeException(dataArray,throwable);
            channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
    private Integer assertTaskJobId(Object o){
        Integer id = null;
        try {
            Method m = o.getClass().getMethod("getTaskJobId");
            if( m != null ){
                id= Integer.parseInt(m.invoke(o).toString());
                return id;
            }
        } catch (InvocationTargetException |IllegalAccessException | NoSuchMethodException e) {
            return id;
        }
        return id;
    }
}
