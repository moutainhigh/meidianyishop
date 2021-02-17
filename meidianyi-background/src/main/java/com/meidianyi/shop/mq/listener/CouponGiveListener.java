package com.meidianyi.shop.mq.listener;

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueBo;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.saas.SaasApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
/**
 * @author lixinguo
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitConfig.QUEUE_COUPON_SEND,
    containerFactory = "simpleRabbitListenerContainerFactory")
public class CouponGiveListener implements BaseRabbitHandler {

	@Autowired
	protected SaasApplication saas;

	@RabbitHandler
    public void handler(@Payload CouponGiveQueueParam param, Message message, Channel channel) {
        log.info("开始调用发券方法");
		CouponGiveQueueBo res = saas.getShopApp(param.getShopId()).coupon.couponGiveService.handlerCouponGive(param);
        log.info("本次发券活动预计发放记录数量（人数*券数）："+param.getUserIds().size()*param.getCouponArray().length);
        log.info("本次发券活动实际发放记录数量："+res.getSuccessSize());
		//更新taskJob进度和状态
        saas.taskJobMainService.updateProgress(Util.toJson(param),param.getTaskJobId(),res.getSuccessSize(),param.getUserIds().size()*param.getCouponArray().length);
        log.info("taskJob进度和状态更新完毕，本次发券活动结束");
	}


    @Override
    public void executeException(Object[] datas, Throwable throwable) {

    }
}
