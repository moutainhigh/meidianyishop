package com.meidianyi.shop.service.foundation.mq.handler;

import java.io.IOException;

import org.springframework.amqp.core.Message;

import com.rabbitmq.client.Channel;

/**
 * MQ消费者默认实现接口 所有消费者都要手动ACK，确保消息被消费
 * 
 * @author 卢光耀
 * @date 2019-08-05 15:58
 *
 */
public interface BaseRabbitHandler {

	/**
	 * 发送成功
	 * 
	 * @param channel mq连接信道
	 * @param message 消息
	 * @throws IOException 异常
	 */
	default void success( Channel channel, Message message) throws IOException {
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}

	/**
	 * 发送失败(消息返回原队列)
	 * 
	 * @param channel mq连接信道
	 * @param message 消息
	 * @throws IOException 异常
	 */
	default void failReturn(Channel channel, Message message) throws IOException {
		channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
	}

	/**
	 * 发送失败(消息不返回原队列,但自己创建的的队列需要设相应的死信队列)
	 * 
	 * @param channel mq连接信道
	 * @param message 消息
	 * @throws IOException 异常
	 */
	default void failNotReturn(Channel channel, Message message) throws IOException {
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
	}

    /**
     * 例外处理
     * @param datas
     * @param throwable
     */
    void executeException(Object[] datas, Throwable throwable);
}
