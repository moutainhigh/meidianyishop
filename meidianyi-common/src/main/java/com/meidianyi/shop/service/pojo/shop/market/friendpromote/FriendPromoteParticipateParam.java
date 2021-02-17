package com.meidianyi.shop.service.pojo.shop.market.friendpromote;
import lombok.Data;
/**
  *  好友助力参与明细
 * @author liangchen
 * @date 2019年8月12日
 */
@Data
public class FriendPromoteParticipateParam {
	/** 好友助力活动Id（商家发起） */
	private Integer promoteId;
	
	/** 用户昵称 */
	private String username;
	
	/** 手机号 */
	private String mobile;
	
	/** 助力活动Id（用户发起） */
	private Integer launchId;

	/** 是否是新用户 promote:是  else：否 */
	private String inviteSource;
	
	/** 分页信息 */
	private Integer currentPage;
	private Integer pageRows;
}
