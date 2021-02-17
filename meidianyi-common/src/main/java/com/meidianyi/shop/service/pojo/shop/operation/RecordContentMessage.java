package com.meidianyi.shop.service.pojo.shop.operation;

/**
 *
 * @author: 卢光耀
 * @date: 2019-07-16 10:57
 *
*/
public class RecordContentMessage {

    public static final String GOODS_CONTENT_ADD = "goods.content.add";
    
    /**
     * 	订单
     */
    /**发货*/
    public static final String ORDER_SHIP = "order.ship";
    /**优惠券礼包订单退款*/
    public static final String ORDER_COUPON_PACK_ORDER_REFUND = "order.coupon.pack.order.refund";
    /**会员卡订单退款*/
    public static final String ORDER_MEMBER_CARD_ORDER_REFUND = "order.member.card.order.refund";
    /**完成*/
    public static final String ORDER_FINISH = "order.finish";
    /**退款退货*/
    public static final String ORDER_RETURN = "order.return";
    /**关闭*/
    public static final String ORDER_CLOSE = "order.close";
    /**核销*/
    public static final String ORDER_VERIFY = "order.verify";
    /**收货*/
    public static final String ORDER_RECEIVE = "order.receive";
    /**提醒*/
    public static final String ORDER_REMIND = "order.remind";
    /**延长收货*/
    public static final String ORDER_EXTEND_RECEIVE = "order.extend.receive";
    /**删除*/
    public static final String ORDER_DELETE = "order.delete";
    
    /**
     * 	会员
     */
    /**	会员发放会员卡 */
    public static final String MSG_MEMBER_CARD_SEND = "member.content.card.send";
    /**	会员余额更新变动 */
    public static final String MSG_MEMBER_ACCOUNT = "member.content.account";
    /**	会员积分更新变动*/
    public static final String MSG_MEMBER_INTEGRALT = "member.content.integral";
    /**	批量禁止{num}名用户登录 */
    public static final String MSG_MEMBER_BATCH_LOGIN_OFF = "member.batch.login.off";
    /**	批量允许{num}名用户登录 */
    public static final String MSG_MEMBER_BATCH_LOGIN_ON = "member.batch.login.on";
    /**	禁止 ID: {userId};昵称：{username}登录 */
    public static final String MSG_MEMBER_LOGIN_OFF = "member.login.off";
    /**	允许 ID: {userId};昵称：{username}登录 */
    public static final String MSG_MEMBER_LOGIN_ON = "member.login.on";

    /**
     * 	营销
     */
    /** 秒杀活动添加 */
    public static final String MARKET_SECKILL_ADD = "market.seckill.add";
    /** 瓜分积分活动添加 */
	public static final String DIVIDE_INTEGRATION_ADD = "divide.integration.add";

    /** 营销分销配置 */
    public static final String DISTRIBUTION_CHANGE = "distribution.change";
    public static final String DISTRIBUTION_STATUS_ON = "distribution.status.on";
    public static final String DISTRIBUTION_STATUS_OFF = "distribution.status.off";
    public static final String DISTRIBUTION_JUDGE_STATUS_ON = "distribution.judge.status.on";
    public static final String DISTRIBUTION_JUDGE_STATUS_OFF = "distribution.judge.status.off";
    public static final String DISTRIBUTION_WITHDRAW_STATUS_ON = "distribution.withdraw.status.on";
    public static final String DISTRIBUTION_WITHDRAW_STATUS_OFF = "distribution.withdraw.status.off";
    public static final String DISTRIBUTION_VALID_1 = "distribution.validity.1";
    public static final String DISTRIBUTION_VALID_2 = "distribution.validity.2";
    public static final String DISTRIBUTION_VALID_3 = "distribution.validity.3";
    public static final String DISTRIBUTION_PROTECT_DATA_1 = "distribution.protect.date.1";
    public static final String DISTRIBUTION_PROTECT_DATA_2 = "distribution.protect.date.2";
    public static final String DISTRIBUTION_PROTECT_DATA_3 = "distribution.protect.date.3";
    public static final String DISTRIBUTION_UPDATE_LEVEL = "distribution.update.level";


}
