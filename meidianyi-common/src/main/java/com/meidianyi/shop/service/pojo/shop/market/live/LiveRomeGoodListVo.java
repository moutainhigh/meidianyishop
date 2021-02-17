package com.meidianyi.shop.service.pojo.shop.market.live;

import java.math.BigDecimal;
import java.util.List;

import com.meidianyi.shop.service.pojo.shop.goods.recommend.GoodsLabelsVo;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 * @time 下午4:32:25
 */
@Data
public class LiveRomeGoodListVo extends LiveGoodVo {
	private String goodsName;
	private String goodsSn;
	private BigDecimal shopPrice;
	private Integer goodsNumber;
	private Integer sortId;
	private Integer brandId;
	private String goodsImg;
	private String sortName;
	private String brandName;
	private List<GoodsLabelsVo> goodsTag;
}
