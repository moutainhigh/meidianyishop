package com.meidianyi.shop.mq.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.shop.recommend.SendCollectBean;
import com.meidianyi.shop.service.saas.SaasApplication;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 好物圈收藏
 * @author zhaojianqiang
 *
 * 2019年11月18日 下午1:30:45
 */
@Slf4j
@Component
@RabbitListener(queues = { RabbitConfig.QUEUE_WX_MALL_ADDSHOPPINGLIST }, containerFactory = "simpleRabbitListenerContainerFactory")
public class WxMallCollectionListener implements BaseRabbitHandler {

	@Autowired
	private SaasApplication saas;

	@RabbitHandler
	public void handler(@Payload SendCollectBean param, Message message, Channel channel) {
		if(param.getStatus().equals(1)) {
			//插入
			log.info("更新收藏");
			WxOpenResult addshoppinglistAdd = saas.getShopApp(param.getShopId()).recommendService.collectionMallService.addshoppinglistAdd(param.getBean());
			log.info("更新收藏结果："+addshoppinglistAdd.isSuccess());
		}
		if(param.getStatus().equals(2)) {
			//删除
			log.info("删除收藏");
			WxOpenResult addshoppinglistDel = saas.getShopApp(param.getShopId()).recommendService.collectionMallService.addshoppinglistDel(param.getBean());
			log.info("删除收藏结果："+addshoppinglistDel.isSuccess());
		}
		log.info("收藏更改状态："+param.getTaskJobId());
		saas.taskJobMainService.updateProgress(Util.toJson(param),param.getTaskJobId(),0,1);
	}

	@Override
	public void executeException(Object[] datas, Throwable throwable) {

	}
}