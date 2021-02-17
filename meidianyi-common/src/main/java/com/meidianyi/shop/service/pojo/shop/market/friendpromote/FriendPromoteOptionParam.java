package com.meidianyi.shop.service.pojo.shop.market.friendpromote;
import lombok.Data;
/**
 * 好友助力活动操作
 * @author liangchen
 * @date 2019年8月8日
 */
@Data
public class FriendPromoteOptionParam {
	/** 活动Id */
	private Integer id;
	
	/** 是否停用标识 */
	public static final Byte BLOCKED = 0;
	public static final Byte NOT_BLOCK = 1;
	
	/** 删除标识 */
	public static final Byte DELETED = 1;
}
