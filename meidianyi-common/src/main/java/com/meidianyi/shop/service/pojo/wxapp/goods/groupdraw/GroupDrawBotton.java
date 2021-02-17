package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序拼团抽奖参团详情
 * 
 * @author zhaojianqiang
 * @time 下午2:18:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDrawBotton {
	private Byte isToInvite;
	private Byte isJoinDraw;
	private Byte isOpenDraw;

}
