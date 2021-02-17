package com.meidianyi.shop.service.pojo.shop.member.card.show;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
/**
 * 	会员卡使用统计数据
 * @author 黄壮壮
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardUseStats {
	/**
	 * 已领取卡的用户数
	 */
	private Integer haveCardUser;
	
	/**
	 * 已领卡数量
	 */
	private Integer haveReceivedNum;
	
	/**
	 * 	可正常使用的卡数量
	 */
	private Integer haveNormalNum;
}
