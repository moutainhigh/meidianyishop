package com.meidianyi.shop.mq.listener;

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsGoodsMqParam;
import com.meidianyi.shop.service.saas.SaasApplication;
import com.meidianyi.shop.service.shop.goods.es.EsGoodsCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author luguangyao
 */
@Component
@Slf4j
@RabbitListener(queues = RabbitConfig.QUEUE_ES_GOODS,
    containerFactory = "currentSimpleRabbitListenerFactory")
public class EsGoodsListener implements BaseRabbitHandler {

    @Autowired
    protected SaasApplication saas;

    @RabbitHandler
    public void handler(@Payload EsGoodsMqParam param, Message message, Channel channel) {
        log.info("\n消费{}",param.getShopId());
        Integer shopId = param.getShopId();
        List<Integer> ids= param.getIdList();
        EsGoodsCreateService esGoodsCreateService = saas.getShopApp(param.getShopId()).esGoodsCreateService;
        if( DBOperating.DELETE.equals(param.getOperate()) ){
            esGoodsCreateService.deleteEsGoods(ids,shopId);
        }else{
            esGoodsCreateService.batchUpdateEsGoodsIndex(ids,shopId);
        }

    }

    @Override
    public void executeException(Object[] datas, Throwable throwable) {

    }
}
