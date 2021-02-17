package com.meidianyi.shop.service.pojo.shop.market.message.msgrecordconfig;

/**
 * service_message_record表中对应的一些字段的定义
 * 
 * @author zhaojianqiang
 * @time 上午9:55:22
 */
public class MessageRecordType {

	/** request_action 字段对应的 */
	/** 短信平台请求 */
	public static final Byte REQUEST_ACTION_SMS = 100;
	/** 系统发送模板消息记录 */
	public static final Byte SYSTEM_SEND_MESSAGE = 1;

	/** templateType字段对应的 */
	/** 订单类型 */
	public static final Byte TEMPLATE_TYPE_ORDER = 1;
	/** 预约类型 */
	public static final Byte TEMPLATE_TYPE_APPOINTMENT = 2;
	/** 优惠券类型 */
	public static final Byte TEMPLATE_TYPE_COUPON = 3;
	/** 拼团类型 */
	public static final Byte TEMPLATE_TYPE_GROUP = 4;
	/** 会员卡类型 */
	public static final Byte TEMPLATE_TYPE_CARD = 5;
	/** 分销 */
	public static final Byte TEMPLATE_TYPE_DISTRIBUTION = 6;
	/** 商家自定义 */
	public static final Byte TEMPLATE_TYPE_CUSTOM = 7;
	/** 砍价 */
	public static final Byte TEMPLATE_TYPE_BARGAIN = 8;
	/** 审核 */
	public static final Byte TEMPLATE_TYPE_AUDIT = 9;
	/** 抽奖 */
	public static final Byte TEMPLATE_TYPE_DRAW = 10;

}
