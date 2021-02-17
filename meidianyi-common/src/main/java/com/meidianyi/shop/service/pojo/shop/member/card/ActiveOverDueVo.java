package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年11月5日
* @Description: 会员卡激活审核超时的记录
*/
@Data
public class ActiveOverDueVo {
	private String cardName;
	private Integer cardId;
	private Integer cardNum;
}
