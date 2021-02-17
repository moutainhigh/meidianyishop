package com.meidianyi.shop.service.pojo.wxapp.card.vo;

import java.sql.Timestamp;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 	会员卡转赠信息
 * @author 黄壮壮
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardGiveVo {
	/**
	 * 	是否可以转赠 0不可以 | 1可以
	 */
	private Byte canGiveWay;
	/**
	 * 	引起不允许转赠原因，存在的订单号
	 */
	private String cardOrderSn;
	/**
	 *	 转赠活动开始时间
	 */
	private Timestamp createTime;
	/**
	 * 	转赠活动截止时间
	 */
	private Timestamp deadline;
	
	/**
	 * 	领取改转赠卡的用户名称
	 */
	private String giveUsername;
	
	/**
	 * 	领取该转赠卡的时间
	 */
	private Timestamp giveAwayTime;
	/**
	 * 	转赠图片(相对路径)
	 */
	private String shareImg;
	/**
	 * 	转赠图片(绝对路径)
	 */
	private String fullShareImg;
}
