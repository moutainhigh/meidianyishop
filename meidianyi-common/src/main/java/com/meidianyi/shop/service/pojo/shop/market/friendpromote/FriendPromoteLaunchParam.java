package com.meidianyi.shop.service.pojo.shop.market.friendpromote;
import lombok.Data;
/**
  *  好友助力发起明细
 * @author liangchen
 * @date 2019年8月12日
 */
@Data
public class FriendPromoteLaunchParam {
	/** 好友助力活动Id（商家发起） */
	private Integer promoteId;
	
	/** 领取用户昵称 */
	private String username;
	
	/** 领取用户手机号 */
	private String mobile;
	
	/** 助力活动Id（用户发起） */
	private Integer id;

	/** 是否助力成功 */
	public static final Byte PROMOTE_STATUS_DEFAULT = -1;
	private Byte promoteStatus = PROMOTE_STATUS_DEFAULT;
	
	/** 分页信息 */
	private Integer currentPage;
	private Integer pageRows;
}
