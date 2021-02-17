package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;
/**
 * 好友助力领取明细
 * @author liangchen
 * @date 2019年8月8日
 */
@Data
public class FriendPromoteReceiveParam {
	/** 商品or优惠券标识 */
	public static final Integer COUPON = 2;
	
	/** 好友助力活动Id（商家发起） */
	private Integer promoteId;
	
	/** 领取用户昵称 */
	private String username;
	
	/** 领取用户手机号 */
	private String mobile;
	
	/** 助力活动Id（用户发起） */
	private Integer id;

	/** 是否已领取 */
	public static final Byte PROMOTE_STATUS_DEFAULT = -1;
	public static final Byte RECEIVED = 2;
	private int promoteStatus = PROMOTE_STATUS_DEFAULT;
	
	/** 订单号 */
	private String orderSn;
	
	/** 分页信息 */
	private Integer currentPage;
	private Integer pageRows;
}
