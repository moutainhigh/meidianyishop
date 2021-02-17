package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import java.util.List;

import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序拼团抽奖参团详情出参
 * 
 * @author zhaojianqiang
 * @time 下午2:18:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDrawInfoReturnVo {
	private GoodsView goods;
	private GroupDrawInfoVo groupDraw;
	private List<GoodsSmallVo> drawGoods;
	private GroupJoinDetailVo groupJoinDetail;
	private Long surplusSecond;
	private GroupDrawList userGroupInfo;
}
