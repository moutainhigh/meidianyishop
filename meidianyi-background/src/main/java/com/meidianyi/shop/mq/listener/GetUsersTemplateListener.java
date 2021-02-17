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
import com.meidianyi.shop.service.pojo.shop.market.message.BindRabbitParam;
import com.meidianyi.shop.service.saas.SaasApplication;

/**
 * 获取关注公众号的用户信息
 * 
 * @author zhaojianqiang
 *
 *         2019年9月2日 下午5:05:04
 */
@Component
@RabbitListener(queues = { RabbitConfig.QUEUE_MA_MAP_BIND }, containerFactory = "simpleRabbitListenerContainerFactory")
public class GetUsersTemplateListener implements BaseRabbitHandler {

	@Autowired
	private SaasApplication saas;

	@RabbitHandler
	public void handler(@Payload BindRabbitParam param, Message message, Channel channel) {
		
		saas.shop.officeAccount.batchGetUsers(param.getAppId(), param.getLanguage(), param.getSysId());
		saas.taskJobMainService.updateProgress(Util.toJson(param),param.getTaskJobId(),0,1);
	}

	@Override
	public void executeException(Object[] datas, Throwable throwable) {

	}
}
