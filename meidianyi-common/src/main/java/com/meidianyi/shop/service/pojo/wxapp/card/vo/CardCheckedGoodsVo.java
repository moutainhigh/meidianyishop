package com.meidianyi.shop.service.pojo.wxapp.card.vo;
/**
 * 限次卡已选商品出参
 * @author 黄壮壮
 *
 */

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.wxapp.user.UserCheckedGoodsVo;

import lombok.Data;
@Data
public class CardCheckedGoodsVo {
	private PageResult<UserCheckedGoodsVo> goodsList;
	private Integer totalNumber;
}
