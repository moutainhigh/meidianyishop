package com.meidianyi.shop.service.pojo.shop.operation;
/**
* @author 黄壮壮
* @Date: 2019年8月22日
* @Description: 交易记录涉及到的交易状态，交易类型，交易内容内聚类
*/
public enum RecordTradeEnum {

	/** 默认的用户 */
	DEFAULT_ADMIN(0),

	/**----------------------------------------
	 *  以下为 trade_status
	 ----------------------------------------*/
	/** 交易状态-已入账 */
	TRADE_STATUS_ALREADY_IN(0),
	/** 交易状态-已出账 */
	TRADE_STATUS_ALREADY_OUT(1),

	
	
	/**----------------------------------------
	 *  以下为 trade_flow
	 ----------------------------------------*/

	/** 资金流向-收入  */
	TRADE_FLOW_IN(0),
	/** 资金流向-支出  */
	TRADE_FLOW_OUT(1),
	/** 资金流向-待确认收入  */
	TRADE_FLOW_TO_BE_CONFIRMED(2),
	
	
	/**----------------------------------------
	 *  以下为 trade_content 交易内容
	 ----------------------------------------*/

	/** 交易内容 - 现金*/
	TRADE_CONTENT_CASH(0),
	/** 交易内容 - 积分*/
	TRADE_CONTENT_SCORE(1),
	
	
	

	/**----------------------------------------
	 * 以下为交易内容为现金所对应的交易类型说明 trade_type
	 ----------------------------------------*/
	/** 交易类型默认值 */
	TYPE_DEFAULT(0),
	/** 微信支付 */
	TYPE_CRASH_WX_PAY(1),
	/** 余额支付 */
	TYPE_CRASH_ACCOUNT_PAY(2),
	/**会员卡支付 */
	TYPE_CRASH_MEMBER_CARD_PAY(3),
	/** 现金退款 */
	TYPE_CASH_REFUND(4),
	/** 用户余额退款 */
	TYPE_CRASH_MACCOUNT_REFUND(5),
	/** 用户会员卡余额退款 */
	TYPE_CRASH_MCARD_ACCOUNT_REFUND(6),
	/** 返利 */
	TYPE_CRASH_REBATE(7),
	/** 抽奖获得余额 */
	TYPE_CRASH_LOTTERY(8),
	/** 用户余额充值 */
	TYPE_CRASH_POWER_MACCOUNT(9),
	/** 用户会员卡余额充值 */
	TYPE_POWER_MCARD_ACCOUNT(10),
	/** 支付有礼*/
	TYPE_CRASH_PAY_AWARD(11),
	/** 用户导入*/
	USER_IMPORT(16),
	/**----------------------------------------
	 * 以下为交易内容为积分所对应的交易类型说明 trade_type
	 ----------------------------------------*/

	/** 积分支付 */
	TYPE_SCORE_PAY(1),
	/** 积分兑换 */
	TYPE_SCORE_EXCHANGE(2),
	/** 幸运大抽奖消耗积分 */
	TYPE_SCORE_LUCKY_LOTTERY(3),
	/** 积分充值 */
	TYPE_SCORE_POWER(4),
	/** 用户登录送积分 */
	TYPE_SCORE_LOGIN(5),
	/** 用户签到送积分 */
	TYPE_SCORE_SIGN(6),
	/** 开卡赠送积分 */
	TYPE_SCORE_CREATE_CARD(7),
	/** 买单送积分 */
	TYPE_SCORE_PAY_BILL(8),
	/** 交易退货退积分 */
	TYPE_SCORE_REFUND(9),
	/** 组团瓜分积分 */
	TYPE_SCORE_GROUP_DIVIDING(10),
	/** 抽奖获得积分 */
	TYPE_SCORE_LOTTERY(11),
	/** 支付有礼 */
	TYPE_SCORE_PAY_AWARD(12),
	/**表单统计*/
	TYPE_FORM_DECORATION_GIFT(13),

	/**--------------------*/
	
	
	
	/**----------------------------------------
	 *  支付类型paid
	 ----------------------------------------*/
	/** 充值 */
	UACCOUNT_RECHARGE(0),
	/** 消费 */
	UACCOUNT_CONSUMPTION(1),
    /** 分销 */
    UACCOUNT_WITHDRAW(2),

	/** 积分变动是否来自退款 */
	IS_FROM_REFUND_Y(1),
	IS_FROM_REFUND_N(0);
	
	private Byte val;
	RecordTradeEnum(int value) {
		this.val = Byte.valueOf(String.valueOf(value));
	}

	public Byte val() {
		return this.val;
	}
}