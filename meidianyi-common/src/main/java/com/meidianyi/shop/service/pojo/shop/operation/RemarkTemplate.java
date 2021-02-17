package com.meidianyi.shop.service.pojo.shop.operation;
/**
 * @author 黄壮壮
 * 备注模块:
 * 用户输入: 0, 代表的是用户自己输入
 * 登录签到相关:1开头四位数，如1001,
 * 订单相关: 2开头四位数，如2001,
 * 会员卡相关:3开头四位数，如3001,
 * 营销相关: 4开头四位数，如4001,
 * 管理员操作相关：5开头四位数，如5001,
 * 依次类推)
 *
 */
public enum RemarkTemplate {
	/**	用户输入 */
	USER_INPUT_MSG(0,null),


    /**
	 * 登录
	 */
	/**	每日登录送积分 */
	LOGIN_EVERY_DAY_SEND(1001,RemarkMessage.MSG_LOGIN_EVERY_DAY_SEND),
	/**	连续签到{0}天，获得{1}积分 */
	SIGN_SOME_DAY_SEND(1002,RemarkMessage.MSG_SIGN_SOME_DAY_SEND),


    /**
	 * 订单
	 */
	/**	订单: {订单号}退余额 */
	ORDER_RETURN(2001,RemarkMessage.MSG_ORDER_RETURN),
	ORDER_CANCEL(2002,RemarkMessage.MSG_ORDER_CANCEL_RETURN),
	ORDER_CLOSE(2003,RemarkMessage.MSG_ORDER_CLOSE_RETURN),
	ORDER_CLOSE_SCORE_ACCOUNT(2004,RemarkMessage.MSG_ORDER_CLOSE_SC_AC),
	ORDER_MAKE(2005,RemarkMessage.MSG_ORDER_MAKE),
	ORDER_VIRTUAL_RETURN(2006,RemarkMessage.MSG_ORDER_VIRTUAL_RETURN),
	ORDER_VIRTUAL_RETURN_SCORE_ACCOUNT(2007,RemarkMessage.MSG_ORDER_VIRTUAL_RETURN_SC_AC),
	ORDER_CANCEL_SCORE_ACCOUNT(2008,RemarkMessage.MSG_ORDER_CANCEL_SCORE_ACCOUNT),
	ORDER_RETURN_SCORE_ACCOUNT(2009,RemarkMessage.MSG_ORDER_RETURN_SCORE_ACCOUNT),
	ORDER_STORE_SCORE(2010,RemarkMessage.MSG_ORDER_STORE_SCORE),
	/**	订单取消，订单会员卡余额支付退款 */
	ORDER_CANCEL_RETURN_CARD_ACCOUNT(2011,RemarkMessage.MSG_ORDER_CANCEL_RETURN_CARD_ACCOUNT),
	/**	订单关闭，订单会员卡余额支付退款 */
	ORDER_CLOSE_RETURN_CARD_ACCOUNT(2012,RemarkMessage.MSG_ORDER_CLOSE_RETURN_CARD_ACCOUNT),
	/**	订单下单会员卡余额支付{订单号}	*/
	ORDER_MAKE_CARD_ACCOUNT_PAY(2013,RemarkMessage.MSG_ORDER_MAKE_CARD_ACCOUNT_PAY),
	/**	订单会员卡余额支付退款 */
	ORDER_RETURN_CARD_ACCOUNT(2014,RemarkMessage.MSG_ORDER_RETURN_CARD_ACCOUNT),
	/**	虚拟订单退款	*/
	ORDER_VIRTUAL_RETURN_DEFAULT(2015,RemarkMessage.MSG_ORDER_VIRTUAL_RETURN_DEFAULT),
    /**	订单完成送积分	*/
    ORDER_FINISH_SEND_SCORE(2016,RemarkMessage.MSG_ORDER_FINISH_SEND_SCORE),
    /**	订单完成返利	*/
    ORDER_REBATE(2017,RemarkMessage.MSG_ORDER_REBATE),
    /**	限次卡退次数	*/
    ORDER_RETURN_LIMIT_CARD(2018,RemarkMessage.MSG_ORDER_RETURN_LIMIT_CARD),
    /**	兑换商品	*/
    ORDER_LIMIT_EXCHGE_GOODS(2019,RemarkMessage.MSG_ORDER_LIMIT_EXCHGE_GOODS),

    /**
	 * 会员卡
	 */
	/**	领卡赠送 */
	CARD_RECEIVE_SEND(3001,RemarkMessage.MSG_CARD_RECEIVE_SEND),
	/**	卡升级赠送 */
	CARD_UPGRADE(3002,RemarkMessage.MSG_CARD_UPGRADE),
	/** 发卡原因： 管理员发卡 - 门店服务次数  */
	SEND_CARD_REASON(3003,RemarkMessage.SEND_CARD_REASON),
	/** 管理员发卡 */
	ADMIN_SEND_CARD(3004,RemarkMessage.ADMIN_SEND_CARD),
	/** 开卡赠送 */
	OPEN_CARD_SEND(3005,RemarkMessage.OPEN_CARD_SEND),
	/**	系统检测升级 */
	SYSTEM_UPGRADE(3006,RemarkMessage.SYSTEM_UPGRADE),
    /**	会员卡续费 */
    CARD_RENEW(3007,RemarkMessage.CARD_RENEW),
    /**	会员卡续费:  {订单号} */
    CARD_RENEW_ORDER(3008,RemarkMessage.CARD_RENEW_ORDER),
    // 继承转赠卡：{0} - 门店服务次数
    CARD_GIVE_COUNT(3009,RemarkMessage.CARD_GIVE_COUNT),
    //  继承转赠卡：{0} - 兑换服务次数
    CARD_GIVE_EXCHANGCOUNT(3010,RemarkMessage.CARD_GIVE_EXCHANGCOUNT),

	/**
	 * 营销
	 */
    /**	分销提现	*/
    DISTRIBUTION_WITHDRAW(2020,RemarkMessage.MSG_DISTRIBUTION_WITHDRAW),
    /**	分销恢复金额	*/
    DISTRIBUTION_THAW(2021,RemarkMessage.MSG_DISTRIBUTION_THAW),
	/**	支付有礼 */
	PAY_HAS_GIFT(4001,RemarkMessage.MSG_PAY_HAS_GIFT),
	/**	评价有理 */
	COMMENT_HAS_GIFT(4002,RemarkMessage.MSG_COMMENT_HAS_GIFT),
	/**	收藏有礼 */
	COLLECT_HAS_GIFT(4003,RemarkMessage.MSG_COLLECT_HAS_GIFT),
	/**	瓜分积分 */
	DIVIDE_SCORE(4004,RemarkMessage.MSG_DIVIDE_SCORE),
	/**	领取优惠券 */
	RECEIVE_COUPON(4005,RemarkMessage.MSG_RECEIVE_COUPON),
	/**	好友助力失败 */
    FRIENDS_HELP_FAIL(4006, RemarkMessage.MSG_FRIENDS_HELP_FAIL),
	/**
     * 分享有礼
     */
    SHARE_HAS_GIFT(4007, RemarkMessage.MSG_SHARE_HAS_GIFT),
	/**
     * 开屏有礼
     */
    ENTER_HAS_GIFT(4008, RemarkMessage.MSG_ENTER_HAS_GIFT),
	/**
	 * 抽奖
	 */
	MSG_LOTTERY_GIFT(4009,RemarkMessage.MSG_LOTTERY_GIFT),
	/** 门店支付 */
	STORE_PAYMEMBT(4010,RemarkMessage.STORE_PAYMEMBT),
	/**表单统计*/
	MSG_FORM_DECORATION_GIFT(4011,RemarkMessage.MSG_FORM_DECORATION_GIFT),
    /**优惠券礼包下单 */
    COUPON_PACK_ORDER(4012,RemarkMessage.MSG_COUPON_PACK_ORDER),



    /**
     * 管理员
     */
	/**	管理员操作 */
	ADMIN_OPERATION(6001,RemarkMessage.MSG_ADMIN_OPERATION),
	/**	管理员测试 */
	ADMIN_OPERATION_TEST(6002,RemarkMessage.MSG_ADMIN_OPERATION_TEST),
	/**	管理员操作- 兑换商品数量 */
	ADMIN_EXCHANGE_GOODS(6003,RemarkMessage.MSG_ADMIN_EXCHANGE_GOODS),
	/**	管理员操作 - 门店服务次数 */
	ADMIN_STORE_SERIVICE(6004,RemarkMessage.MSG_ADMIN_STORE_SERIVICE),
	/**	管理员操作 - 会员卡余额 */
	ADMIN_CARD_ACCOUNT(6005,RemarkMessage.MSG_ADMIN_CARD_ACCOUNT),
	/** 用户激活奖励送积分*/
	ADMIN_USER_IMPORT(6006,RemarkMessage.MSG_ADMIN_USER_IMPORT),
	/** 用户激活奖励送积分*/
	ADMIN_USER_ACTIVATE(6007,RemarkMessage.MSG_ADMIN_USER_ACTIVATE);

	/**	返回码 */
	public  Integer code;
	/**	返回信息 */
	private String message;

	private RemarkTemplate(Integer code,String message) {
		this.code = code;
		this.message = message;
	}

	public static String getMessageByCode(Integer code) {
		for(RemarkTemplate item: RemarkTemplate.values()) {
			if(item.code.equals(code)) {
				return item.message;
			}
		}
		return null;
	}
}
