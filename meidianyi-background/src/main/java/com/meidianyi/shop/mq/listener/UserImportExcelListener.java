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
import com.meidianyi.shop.service.pojo.shop.member.userimp.UserImportMqParam;
import com.meidianyi.shop.service.saas.SaasApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户导入
 * 
 * @author zhaojianqiang
 * @time 下午3:20:15
 */
@Component
@Slf4j
@RabbitListener(queues = { RabbitConfig.QUEUE_EXCEL }, containerFactory = "simpleRabbitListenerContainerFactory")
public class UserImportExcelListener implements BaseRabbitHandler {

	@Autowired
	private SaasApplication saas;

	@RabbitHandler
	public void handler(@Payload UserImportMqParam param, Message message, Channel channel) {
		log.info("用户导入");
		saas.getShopApp(param.getShopId()).member.userImportService.checkList(param.getModels(), param.getCardId(),
				param.getGroupId(), param.getTagId(),param.getLang());
		log.info("用户导入结束");
		log.info("param.getTaskJobId()"+param.getTaskJobId());
	    saas.taskJobMainService.updateProgress(Util.toJson(param),param.getTaskJobId(),0,1);
	}

	@Override
	public void executeException(Object[] datas, Throwable throwable) {

	}
}
