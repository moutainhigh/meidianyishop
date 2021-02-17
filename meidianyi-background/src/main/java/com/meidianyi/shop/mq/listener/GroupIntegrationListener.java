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
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupInteRabbitParam;
import com.meidianyi.shop.service.saas.SaasApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * 组团瓜分积分开奖
 * 
 * @author zhaojianqiang
 * @time 下午2:23:20
 */
@Slf4j
@Component
@RabbitListener(queues = {
		RabbitConfig.QUEUE_GROUP_INTEGRATION_SUCCESS }, containerFactory = "simpleRabbitListenerContainerFactory")
public class GroupIntegrationListener implements BaseRabbitHandler {

	@Autowired
	private SaasApplication saas;

	@RabbitHandler
	public void handler(@Payload GroupInteRabbitParam param, Message message, Channel channel) {
		// 插入
		log.info("队列组团瓜分积分开奖开始；groupId：{}，actId：{}",param.getGroupId(),param.getPinInteId());
		saas.getShopApp(param.getShopId()).groupIntegration.asyncSuccessGroupIntegration(param);
		log.info("队列组团瓜分积分开奖结束");
		saas.taskJobMainService.updateProgress(Util.toJson(param), param.getTaskJobId(), 0, 1);
	}

	@Override
	public void executeException(Object[] datas, Throwable throwable) {

	}
}