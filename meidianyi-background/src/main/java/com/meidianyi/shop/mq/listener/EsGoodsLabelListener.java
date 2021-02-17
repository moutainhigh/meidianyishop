package com.meidianyi.shop.mq.listener;

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.jedis.data.label.MqEsGoodsLabel;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.saas.SaasApplication;
import com.meidianyi.shop.service.shop.goods.es.goods.label.EsGoodsLabelCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * ElasticSearch update goods label data
 * @author luguangyao
 * @date 2019/11/21
 *
*/
@Slf4j
@Component
@RabbitListener(queues = { RabbitConfig.QUEUE_ES_LABEL }, containerFactory = "simpleRabbitListenerContainerFactory")
public class EsGoodsLabelListener implements BaseRabbitHandler {

    @Autowired
    private SaasApplication saasApplication;


    @RabbitHandler
    public void success(@Payload MqEsGoodsLabel param, Message message, Channel channel) throws IOException {
        Integer shopId = param.getShopId();
        log.info("\n【ES商品标签MQ】开始处理来自{}店铺的数据",shopId);
        StringBuilder dataLog = new StringBuilder();
        List<Integer> goodsIds = param.getGoodsIds();
        List<Integer> labelIds = param.getLabelIds();
        EsGoodsLabelCreateService esGoodsLabelCreateService = saasApplication.getShopApp(shopId).esGoodsLabelCreateService;
        if( !CollectionUtils.isEmpty(goodsIds) ){
            dataLog.append("\n【ES商品标签MQ商品ID】").append(goodsIds.toString());
            esGoodsLabelCreateService.createEsLabelIndexForGoodsId(goodsIds,param.getOperating());
        }
        if( !CollectionUtils.isEmpty(labelIds) ){
            dataLog.append("\n【ES商品标签MQ标签ID】").append(labelIds.toString());
            esGoodsLabelCreateService.createEsLabelIndexForLabelId(labelIds.get(0),param.getOperating());
        }
        log.info(dataLog.toString());
    }

    @Override
    public void executeException(Object[] datas, Throwable throwable) {


    }
}
