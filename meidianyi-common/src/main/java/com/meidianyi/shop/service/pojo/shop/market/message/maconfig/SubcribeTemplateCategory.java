package com.meidianyi.shop.service.pojo.shop.market.message.maconfig;

import lombok.Data;

/**
 *
 * @author zhaojianqiang
 *
 *         2019年12月5日 上午11:23:19
 */
@Data
public class SubcribeTemplateCategory {

	/**
	 * 去SubscribeMessageConfig中找templeName相同的，对应就是需要的
	 */

	/** 抽奖结果通知 */
	public static final String DRAW_RESULT = "draw_result";
	/** 审核结果通知 */
	public static final String AUDIT = "audit";
	/** 会员等级变更通知 */
	public static final String USER_GRADE = "user_grade";
	/** 积分变更提醒 */
	public static final String SCORE_CHANGE = "score_change";
	/** 订单发货通知 */
	public static final String ORDER_DELIVER = "order_deliver";
	/** 退款通知 */
	public static final String REFUND_RESULT = "refund_result";
	/** 邀请成功通知 */
	public static final String INVITE_SUCCESS = "invite_success";
	/** 余额变动 */
	public static final String BALANCE_CHANGE = "balance_change";
	/** 新问诊订单 */
	public static final String CONSULTATION_ORDER_PAY = "consultation_order_pay";
    /** 用户咨询订单超时*/
    public static final String CONSULTATION_ORDER_EXPIRE = "consultation_order_expire";
    /** 咨询回复通知*/
    public static final String CONSULTATION_ANSWER = "consultation_answer";
    /** 医生已接诊提醒*/
    public static final String CONSULTATION_SUCCESS = "consultation_success";
    /** 新订单提醒*/
    public static final String ORDER_NEW = "order_new";

}
