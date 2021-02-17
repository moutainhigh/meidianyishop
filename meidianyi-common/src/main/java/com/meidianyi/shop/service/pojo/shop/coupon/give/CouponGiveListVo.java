package com.meidianyi.shop.service.pojo.shop.coupon.give;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 定向发放优惠券
 * @author liangchen
 * @date 2019年7月29日
 */

@Data
public class CouponGiveListVo {
	/** 活动id */
	private Integer 	id;
	/** 活动名称 */
	private String 	  	actName;
	/** 创建时间 */
	private Timestamp 	createTime;
	/** 发送人群 */
	private String 	  	sendCondition;
	private CouponGiveGrantInfoParam condition;
	/** 发放类型 */
	private Integer 	sendAction;
	/** 发放状态 */
	private Integer 	sendStatus;
	/** 会员卡id */
	private String 		cardId;
	/** 会员卡名称 */
	private List<String> cardName;
	/** 标签id */
	private String 		tagId;
	/** 标签名称 */
	private List<String> tagName;
	/** 几天内有交易记录 */
	private Integer 	havePay;
	/** 几天内无交易记录 */
	private Integer 	noPay;
	/** 累计购买次数小于N次 */
	private Integer 	maxCount;
	/** 累计购买次数大于N次 */
	private Integer 	minCount;
	/** 购买商品均价小于N元 */
	private BigDecimal 	maxAvePrice;
	/** 购买商品均价大于N元 */
	private BigDecimal 	minAvePrice;
	/** 优惠券信息 */
	List<CouponGiveListConditionVo> couponGiveListConditionVo;
	
}
