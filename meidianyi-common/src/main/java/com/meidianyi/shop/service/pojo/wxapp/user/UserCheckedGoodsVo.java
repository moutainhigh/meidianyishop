package com.meidianyi.shop.service.pojo.wxapp.user;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 用户兑换商品出参
 * @author 黄壮壮
 *
 */
@Data
public class UserCheckedGoodsVo {
	private Integer id;
	private Byte action;
	private String identityId;
	private Integer userId;
	private Integer goodsId;
	private Integer productId;
	private Integer goodsNumber;
	private Timestamp createTime;
	private Timestamp updateTime;
	private BigDecimal prdPrice;
	private String prdSn;
	private String prdSpecs;
	private String prdDesc;
	private String prdImg;
	private Integer prdNumber;
	private String goodsName;
	private String goodsSn;
	private BigDecimal marketPrice;
	private String goodsImg;
	private Byte isOnSale;
	private Byte goodsType;
	private Integer limitBuyNum;
	private Integer limitMaxNum;
}
