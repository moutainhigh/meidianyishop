package com.meidianyi.shop.mq.listener;

/**
 * 代付订单退款
 * @author: 王帅
 **/

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipMqParam;
import com.meidianyi.shop.service.saas.SaasApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 批量发货
 * @author 王帅
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConfig.QUEUE_BATCH_SHIP},containerFactory = "simpleRabbitListenerContainerFactory")
public class BatchShipListener implements BaseRabbitHandler {

    @Autowired
    private SaasApplication saas;

    @RabbitHandler
    public void handler(@Payload BatchShipMqParam param, Message message, Channel channel) throws MpException {
        saas.getShopApp(param.getShopId()).writeOrder.executeBatchShip(param);
        //更新taskJob进度和状态
        saas.taskJobMainService.updateProgress(Util.toJson(param), param.getTaskJobId(), 0,1);
    }

    @Override
    public void executeException(Object[] datas, Throwable throwable) {

    }
}
