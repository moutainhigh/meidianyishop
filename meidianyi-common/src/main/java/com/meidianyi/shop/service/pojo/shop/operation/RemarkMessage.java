package com.meidianyi.shop.service.pojo.shop.operation;
/**
 *
 * @author 黄壮壮
 * 	积分备注内容消息
 */
public class RemarkMessage {

	/**
	 * 登录
	 */
	/** 每日登录送积分 */
	public static final String MSG_LOGIN_EVERY_DAY_SEND = "login.every.day.send";
	public static final String MSG_SIGN_SOME_DAY_SEND = "sign.some.day.send";
	/**
	 * 订单
	 */
	public static final String MSG_ORDER_RETURN = "order.return";
	public static final String MSG_ORDER_CANCEL_RETURN = "order.cancel.return";
	public static final String MSG_ORDER_CLOSE_RETURN = "order.close.return";
	public static final String MSG_ORDER_CLOSE_SC_AC = "order.close.score.account";
	public static final String MSG_ORDER_MAKE = "order.make";
	public static final String MSG_ORDER_VIRTUAL_RETURN = "order.virtual.return";
	public static final String MSG_ORDER_VIRTUAL_RETURN_SC_AC = "order.virtual.return.score.account";
	public static final String MSG_ORDER_CANCEL_SCORE_ACCOUNT = "order.cancel.score.account";
	public static final String MSG_ORDER_RETURN_SCORE_ACCOUNT = "order.return.score.account";
	public static final String MSG_ORDER_STORE_SCORE = "order.store.score";
	public static final String MSG_ORDER_CANCEL_RETURN_CARD_ACCOUNT="order.cancel.return.card.account";
	public static final String MSG_ORDER_CLOSE_RETURN_CARD_ACCOUNT="order.close.return.card.account";
	public static final String MSG_ORDER_MAKE_CARD_ACCOUNT_PAY = "order.make.card.account.pay";
	public static final String MSG_ORDER_RETURN_CARD_ACCOUNT = "order.return.card.account";
	public static final String MSG_ORDER_VIRTUAL_RETURN_DEFAULT = "order.virtual.return.default";
    public static final String MSG_ORDER_FINISH_SEND_SCORE = "order.finish.send.score";
    public static final String MSG_ORDER_REBATE = "order.rebate";
    public static final String MSG_ORDER_LIMIT_EXCHGE_GOODS = "order.limit.exchge.goods";
    public static final String MSG_ORDER_RETURN_LIMIT_CARD = "order.return.limit.card";
	/**
	 * 会员卡
	 */
	/** 领卡赠送积分 */
	public static final String MSG_CARD_RECEIVE_SEND = "card.receive.send";
	/** 卡升级赠送积分 */
	public static final String MSG_CARD_UPGRADE = "card.upgrade.send";
	/** 发卡原因： 管理员发卡 - 门店服务次数  */
	public static final String SEND_CARD_REASON = "member.card.charge.money.reason";
	/** 管理发卡 */
	public static final String ADMIN_SEND_CARD = "member.card.admin.send.card";
	/** 开卡赠送 */
	public static final String OPEN_CARD_SEND = "member.card.open.send";
	/** 系统检测升级 */
	public static final String SYSTEM_UPGRADE = "member.card.system.upgrade";
	/**	剩余可享包邮{0}次 */
	public static final String FREESHIP_NUM = "member.card.freeship.num";
	/** 可享受包邮{0}次 */
	public static final String FREESHIP_TOTAL_NUM = "member.card.freeship.total.num";
    /** 会员卡续费 */
    public static final String CARD_RENEW = "member.card.renew";
    /** 会员卡续费: {订单号} */
    public static final String CARD_RENEW_ORDER="member.card.renew.order";
    /**   继承转赠卡：{0} - 门店服务次数 */
    public static final String CARD_GIVE_COUNT="member.card.give.count";
    /**    继承转赠卡：{0} - 兑换服务次数 */
    public static final String CARD_GIVE_EXCHANGCOUNT="member.card.give.exchangCount";
	/**
	 * 登录
	 */


    /**
     * 营销
	 */
    public static final String MSG_DISTRIBUTION_WITHDRAW = "distribution.withdraw";
    public static final String MSG_DISTRIBUTION_THAW = "distribution.thaw";
	/** 支付有礼 */
	public static final String MSG_PAY_HAS_GIFT = "pay.has.gift";
	/**幸运大抽奖*/
	public static final String MSG_LOTTERY_GIFT = "lottery.has.gift";
	/** 评价有礼送积分 */
	public static final String MSG_COMMENT_HAS_GIFT = "comment.has.gift";
	/** 收藏有礼 */
	public static final String MSG_COLLECT_HAS_GIFT = "collect.has.gift";
	/**表单统计*/
	public static final String MSG_FORM_DECORATION_GIFT = "form.Decoration.gift";
	/** 瓜分积分 */
	public static final String MSG_DIVIDE_SCORE = "divide.score";
	/** 领取优惠券 */
	public static final String MSG_RECEIVE_COUPON = "receive.coupon";
	/** 好友助力失败奖励 */
	public static final String MSG_FRIENDS_HELP_FAIL = "friends.help.fail";
    /**
     * 分享有礼
     */
    public static final String MSG_SHARE_HAS_GIFT = "share.has.gift";
    public static final String MSG_ENTER_HAS_GIFT = "enter.has.gift";
	/** 门店支付 */
	public static final String STORE_PAYMEMBT = "member.card.charge.money.payment";
    /**优惠券礼包下单*/
    public static final String MSG_COUPON_PACK_ORDER = "coupon.pack.order.payment";


	/**
	 * 管理员
	 */
	/**	管理员操作 */
	public static final String MSG_ADMIN_OPERATION = "admin.operation";
	public static final String MSG_ADMIN_OPERATION_TEST = "admin.operation.test";
	/**	管理员操作- 兑换商品数量 */
	public static final String MSG_ADMIN_EXCHANGE_GOODS = "admin.exchange.goods";
	/**	管理员操作 - 门店服务次数 */
	public static final String MSG_ADMIN_STORE_SERIVICE = "admin.store.serivice";
	/**	管理员操作 - 会员卡余额 */
	public static final String MSG_ADMIN_CARD_ACCOUNT = "admin.card.account";
	public static final String MSG_ADMIN_USER_IMPORT = "admin.user.import";
	public static final String MSG_ADMIN_USER_ACTIVATE = "admin.user.activate";

}
