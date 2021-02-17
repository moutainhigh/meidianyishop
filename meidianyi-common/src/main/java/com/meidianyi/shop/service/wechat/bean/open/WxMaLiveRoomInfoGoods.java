package com.meidianyi.shop.service.wechat.bean.open;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang 2020年4月2日下午5:02:57
 */
@Data
public class WxMaLiveRoomInfoGoods implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7081843931712826266L;

	@SerializedName("cover_img")
	private String coverImg;

	private String url;
	
	/** price_type为2：最低价；为3：原价 */
	private BigDecimal price;
	
	/** price_type为2：最高价；为3：现价 */
	private BigDecimal price2;
	
	/** 1：一口价；2：价格区间 ；3：显示折扣价*/
	@SerializedName("price_type")
	private Byte priceType;

	private String name;

}
