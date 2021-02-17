package com.meidianyi.shop.service.pojo.shop.market.message.maconfig;

import lombok.Data;

/**
 * @author zhaojianqiang
 */
@Data
public class TypesNames {
	/** 提交申请 */
	public static final String AUDIT_UPGRADE = "audit_upgrade";
	
	/** 添加订单 */
	public static final String ADD_ORDER = "add_order";
	
	/** 邀请成功的：比如拼团邀请的 */
	public static final String INVITE = "invite";
	
	/** 拼团抽奖结果通知 */

	public static final String GROUP_DRAW = "group_draw";
	
	/** 送礼 */
	public static final String GIVE_GIFT = "give_gift";
	
	/** 退款通知 */
	public static final String ORDER_REFUND = "order_refund";
	
	/** 余额提现那用的*/
	public static final String WITH_DRAW = "with_draw";

    /** 新的问诊单*/
    public static final String NEW_CONSULTATION = "new_consultation";
}
