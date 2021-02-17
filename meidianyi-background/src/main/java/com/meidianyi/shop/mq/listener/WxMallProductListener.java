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
import com.meidianyi.shop.service.pojo.shop.recommend.SendProductBean;
import com.meidianyi.shop.service.saas.SaasApplication;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 好物圈相关之物品信息
 * @author zhaojianqiang
 *
 * 2019年11月18日 下午1:30:45
 */
@Slf4j
@Component
@RabbitListener(queues = { RabbitConfig.QUEUE_WX_MALL_IMPORTPRODUCT }, containerFactory = "simpleRabbitListenerContainerFactory")
public class WxMallProductListener implements BaseRabbitHandler {

	@Autowired
	private SaasApplication saas;

	@RabbitHandler
	public void handler(@Payload SendProductBean param, Message message, Channel channel) {
		if(param.getStatus().equals(1)) {
			//插入
			log.info("更新或导入物品信息");
			WxOpenResult importProductUpdate = saas.getShopApp(param.getShopId()).recommendService.productMallService.importProductUpdate(param.getBean());
			log.info("更新或导入物品信息结果："+importProductUpdate.isSuccess());
		}
		//留以后拓展用
		log.info("物品更改状态："+param.getTaskJobId());
		saas.taskJobMainService.updateProgress(Util.toJson(param),param.getTaskJobId(),0,1);
	}

	@Override
	public void executeException(Object[] datas, Throwable throwable) {

	}
}