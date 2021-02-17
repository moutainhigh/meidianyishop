package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 小程序拼团抽奖参团详情
 * 
 * @author zhaojianqiang
 * @time 下午2:18:35
 */
@Data
public class GroupDrawList {
	private Integer id;
	private Integer groupDrawId;
	private Integer goodsId;
	private Integer groupId;
	private Integer userId;
	private Byte isGrouper;
	private Integer inviteUserId;
	private String orderSn;
	private Byte status;
	private Byte drawStatus;
	private Byte isWinDraw;
	private Timestamp openTime;
	private Timestamp endTime;
	private Timestamp drawTime;
	private Integer inviteUserNum;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String userName;
	private String userAvatar;

}
