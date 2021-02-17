package com.meidianyi.shop.mq.listener;

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportMqParam;
import com.meidianyi.shop.service.saas.SaasApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 微铺宝商品excel模板导入Consumer
 * @author 李晓冰
 * @date 2020年03月19日
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConfig.QUEUE_GOODS_VPU_EXCEL_IMPORT},containerFactory = "simpleRabbitListenerContainerFactory")
public class GoodsVpuExcelImportListener implements BaseRabbitHandler {

    @Autowired
    private SaasApplication saas;

    @RabbitHandler
    public void handler(@Payload GoodsVpuExcelImportMqParam param, Message message, Channel channel){
        saas.getShopApp(param.getShopId()).goodsImportService.goodsVpuExcelImportMqCallback(param);
        //更新taskJob进度和状态
        saas.taskJobMainService.updateProgress(Util.toJson(param), param.getTaskJobId(), 0,1);
    }

    @Override
    public void executeException(Object[] datas, Throwable throwable) {

    }
}
