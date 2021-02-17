package com.meidianyi.shop.service.pojo.shop.payment;

/**
 * 支付方式
 * @author lixinguo
 *
 */
public class PayCode {

	public static final String PAY_CODE_WX_PAY = "wxpay";
	public static final String PAY_CODE_SCORE_PAY = "score";
	public static final String PAY_CODE_BALANCE_PAY = "balance";
	public static final String PAY_CODE_COD = "cod";
	public static final String PAY_CODE_MEMBER_CARD = "member_card";

	public static final Byte PAY_CODE_ENABLED = 1;
	public static final Byte PAY_CODE_DISABLED = 0;
}
