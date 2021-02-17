package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import java.util.List;

import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;

import lombok.Data;

/**
 * 小程序拼团抽奖列表出参
 * 
 * @author zhaojianqiang
 * @time 下午2:18:35
 */
@Data
public class GroupDrawVo {
	private List<GoodsSmallVo> list;
	
	private GroupDrawInfoVo groupDraw;

}
