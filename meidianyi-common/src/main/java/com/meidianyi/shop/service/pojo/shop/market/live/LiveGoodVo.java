package com.meidianyi.shop.service.pojo.shop.market.live;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author zhaojianqiang
 */
@Data
public class LiveGoodVo {
	private Integer id;
	private Integer liveId;
	private Integer roomId;
	private Integer goodsId;
	private String coverImg;
	private String url;
	private BigDecimal price;
	private String name;
	private Integer addCartNum;
	private BigDecimal priceEnd;
	private Byte priceType;
	private Byte delFlag;
	private Timestamp delTime;
	
}
