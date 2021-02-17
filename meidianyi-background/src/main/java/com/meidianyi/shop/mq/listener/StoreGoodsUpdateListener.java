package com.meidianyi.shop.mq.listener;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.rabbitmq.client.Channel;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.shop.store.goods.UpdateStoreGoodsMqParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.saas.SaasApplication;
import com.meidianyi.shop.service.shop.store.store.StoreGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author 赵晓东
 * 更新商品监听
 */
@Slf4j
@Component
@RabbitListener(queues = {RabbitConfig.QUEUE_STORE_GOODS_UPDATE}, containerFactory = "simpleRabbitListenerContainerFactory")
public class StoreGoodsUpdateListener implements BaseRabbitHandler {

    @Autowired
    private SaasApplication saasApplication;

    @RabbitHandler
    public void handler(@Payload UpdateStoreGoodsMqParam param, Message message, Channel channel) throws WxPayException {
        log.info("\n开始更新门店 ： " + param.getParam().getStoreId() + "的商品信息");

        StoreGoodsService storeGoodsService = saasApplication.getShopApp(param.getShopId()).storeGoodsService;
        if (param.getParam() != null) {
            StorePojo storePojo = saasApplication.getShopApp(param.getShopId()).store.getStore(param.getParam().getStoreId());
            if( storePojo == null ){
                log.error("[门店商品][商品同步]---id【{}】店铺信息找不到",param.getParam().getStoreId());
                return;
            }
            try {
                storeGoodsService.updateGoodsDataFromShop(param.getParam());
            }catch (Exception e){
                log.error("更新门店异常为 :{} ", ExceptionUtils.getStackTrace(e));
            }
        }
        //更新taskJob进度和状态
        saasApplication.taskJobMainService.updateProgress(Util.toJson(param), param.getTaskJobId(), 0, 1);
        String key = StoreGoodsService.UPDATE_IS_COMPILE + param.getShopId() + ":" + param.getParam().getStoreId();
        storeGoodsService.jedis.delete(key);


        log.info("\n更新门店 ： " + param.getParam().getStoreId() + "的商品信息结束");
    }
    @Override
    public void executeException(Object[] datas, Throwable throwable) {
        log.error("更新门店商品任务出错" + datas);
    }
}
