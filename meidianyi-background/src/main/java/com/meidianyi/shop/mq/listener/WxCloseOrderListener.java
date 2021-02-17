package com.meidianyi.shop.mq.listener;

/**
 * 微信接口关闭订单
 * @author: 王兵兵
 * @create: 2020-02-04 11:54
 **/

import com.github.binarywang.wxpay.exception.WxPayException;
import com.rabbitmq.client.Channel;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.shop.store.service.order.OrderCloseQueenParam;
import com.meidianyi.shop.service.saas.SaasApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = {RabbitConfig.QUEUE_CLOSE_ORDER},containerFactory = "simpleRabbitListenerContainerFactory")
public class WxCloseOrderListener implements BaseRabbitHandler {
    @Autowired
    private SaasApplication saas;



    @RabbitHandler
    public void handler(@Payload OrderCloseQueenParam param, Message message, Channel channel) throws WxPayException {
        try {
            saas.getShopApp(param.getShopId()).pay.mpPay.wxCloseOrder(param.getOrderSn());
        }catch (WxPayException e){
            log.error("关闭订单接口调用 错误 e",e);
            throw e;
        }
        //更新taskJob进度和状态
        saas.taskJobMainService.updateProgress(Util.toJson(param),param.getTaskJobId(),0,1);

    }


    @Override
    public void executeException(Object[] datas, Throwable throwable) {

    }
}
