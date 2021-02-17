package com.meidianyi.shop.service.pojo.shop.member.card;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.member.card.show.CardUseStats;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 黄壮壮
 * @Date: 2019年8月1日
 * @Description: 会员卡出参
 */

@Data
@NoArgsConstructor
public abstract class BaseCardVo {
	/**
	 * 1-基本信息
	 */
	/** 会员卡id */
	private Integer id;
	/** 会员卡类型 */
	private Byte cardType;
	/** 会员卡头像 */
	private String avatar;
	/** 会员卡名称 */
	private String cardName;

	/** 背景类型 0： 背景色；1：背景图 */
	private Byte bgType;
	/** 背景色 */
	private String bgColor;
	/** 背景图 */
	private String bgImg;


	/**
	 * 领取类型 0：直接领取；1：需要购买；2：需要领取码
	 */
	private Byte isPay;
	/**领取方式 1:领取码 2：卡号+密码*/
	private Byte receiveAction;
	/**
	 * 是否需要激活 0： 否；1： 是
	 */
	private Byte activation;
	/** 是否审核 0： 否；1： 是 */
	private Byte examine;

	/** 激活需要的信息 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String activationCfg;
	/**  专享商品 */
	private List<Integer> ownGoodsId;
	/**  专享商家 */
	private List<Integer> ownStoreCategoryIds;
	/**  专享平台 */
	private List<Integer> ownPlatFormCategoryIds;
	/**  专享品牌 */
	private List<Integer> ownBrandId;

	/**  批次 */
	private List<CardBatchVo> batchList;
	
	/**
	 * 使用门店类型 0：全部门店；1：部分门店；-1：不可在门店使用
	 */
	protected Byte storeListType;
	/** 门店Id */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String storeList;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected List<Integer> storeIdList;
	@JsonProperty("storeList")
	protected List<StoreBasicVo> storeDataList;
	
	/**
	 * 	是否展示充值明细
	 */
	protected Byte showCharge = 0;
	/**
	 * 	是否展示退款明细
	 */
	protected Byte hasRefund = 0;
	/**
	 *	 是否展示续费记录
	 */
	protected Byte renewRecord = 0;
	
	/**
	 * 	会员卡使用统计数据
	 */
	private CardUseStats cardUseStats;

    /**
     * 修改配置
     */
	public abstract void changeJsonCfg();

}
