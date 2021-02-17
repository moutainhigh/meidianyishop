package com.meidianyi.shop.mq.listener;

import org.jooq.Result;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.MarketMqParam;
import com.meidianyi.shop.service.saas.SaasApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * system营销日历
 * 
 * @author zhaojianqiang
 * @date 2020年4月26日下午2:18:13
 */
@Component
@Slf4j
@RabbitListener(queues = { RabbitConfig.QUEUE_CALENDAR }, containerFactory = "simpleRabbitListenerContainerFactory")
public class SystemPushCalendarListener implements BaseRabbitHandler {

	@Autowired
	private SaasApplication saas;

	@RabbitHandler
	public void handler(@Payload MarketMqParam param, Message message, Channel channel) {
		log.info("营销日历推送开始");
		int failSize = 0;
		Result<ShopRecord> result = saas.shop.getAll();
		int allSize = result != null ? result.size() : 0;
		for (ShopRecord shopRecord : result) {
			boolean pushInfo = saas.getShopApp(shopRecord.getShopId()).calendarService.getPushInfo(param);
			if (!pushInfo) {
				failSize++;
			}
		}
		log.info("营销日历推送结束");
		saas.taskJobMainService.updateProgress(Util.toJson(param), param.getTaskJobId(), failSize, allSize);
	}

	@Override
	public void executeException(Object[] datas, Throwable throwable) {

	}
}
