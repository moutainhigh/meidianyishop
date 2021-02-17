package com.meidianyi.shop.service.pojo.shop.member.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGivePopVo;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardCouponPack;
import com.meidianyi.shop.service.pojo.shop.member.card.base.CardMarketActivity;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardTag;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.BUY_BY_CRASH;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.BUY_BY_SCORE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_STP_PART;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_STP_ALL;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_STP_BAN;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MAPPER;

/**
* @author 黄壮壮
* @Date: 2019年8月7日
* @Description:
*/
@Getter
@Setter
@Slf4j
public class NormalCardToVo extends NormalCardVo {
	
	/** 使用须知 */
	private String desc;
	/** 联系方式 */
	private String mobile;

	/** -会员折扣: 全部商品；1代表全部商品，0代表指定商品 */
	private Byte discountIsAll;
	/** -折扣： 商品id */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String discountGoodsId;
	private String[] goodsId;
	/** -折扣： 商家分类id */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String discountSortId;
	private String[] shopCategoryIds;
	/** -折扣: 平台分类id */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String discountCatId;
	private String[] platformCategoryIds;
	/** -折扣 : 品牌分类id */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String discountBrandId;
	private String[] brandId;
	
	/*
	 * 积分具体详情
	 */
	/** 开卡送多少积分 */
	@JsonProperty("score")
	private Integer sorce;
	/** 购物送积分策略json序列化对象 */
	private ScoreJson scoreJson;
	
	
	/*
	 * 卡充值
	 */
	/** 开卡送多少元 */
	private Integer sendMoney;
	/** 卡充值送积分策略json数据 */
	private PowerCardJson powerCardJson;
	
	

	
	/** 购买类型 */
	private Byte payType;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private BigDecimal payFee;
	/**购买类型0 为 现金购买金额 */
	private BigDecimal payMoney;
	/**购买类型1 积分购买 */
	private BigDecimal payScore;
	
	/** 激活需要的信息 */
	private String[] activationCfgBox;

    /** 是否开卡送券：0不是，1是 */
	private Byte sendCouponSwitch;
    /** 送惠类型：0优惠券，1优惠券礼包 */
	private Byte sendCouponType;
    /** 赠送优惠券或礼包id，字符串逗号隔开 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String sendCouponIds;
	private List<Integer> couponIds;
    /** 优惠券详细信息裂变 */
	private List<CouponGivePopVo> couponList;

	/**
	 * 	同步打标签
	 */
	@JsonProperty("cardTag")
	private CardTag myCardTag;

	/**
	 *	不可与优惠券公用的营销活动
	 */
	List<CardMarketActivity> marketActivities;

	/**
	 * 优惠券礼包
	 */
	UserCardCouponPack couponPack;

	/**
	 * 处理策略
	 */
	@Override
	public void changeJsonCfg() {
		log.info("执行NormalCardToVo的处理策略");
		super.changeJsonCfg();

		/* 积分指定商品 处理 */
		/* 商品id */
		if(isNotBlank(discountGoodsId)) {
			goodsId = discountGoodsId.replaceAll("\\s+","").split(",");
		}else {
			goodsId = new String[] {};
		}
		/* 商家分类id */
		if(isNotBlank(discountSortId)) {
			shopCategoryIds = discountSortId.replaceAll("\\s+","").split(",");
		}else {
			shopCategoryIds = new String[] {};
		}
		/*平台分类id */
		if(isNotBlank(discountCatId)) {
			platformCategoryIds = discountCatId.replaceAll("\\s+","").split(",");
		}else {
			platformCategoryIds = new String[] {};
		}
		/* 品牌分类id */
		if(isNotBlank(discountBrandId)) {
			brandId = discountBrandId.replaceAll("\\s+","").split(",");
		}else {
			brandId = new String[] {};
		}



		/* 购物送积分策略json对象 */
		String buyScore = this.getBuyScore();
		if (!StringUtils.isBlank(buyScore)) {
			try {
				log.info("正在解析数据");
				scoreJson = MAPPER.readValue(buyScore, ScoreJson.class);
			} catch (Exception e) {
				log.info("购物积分策略json解析失败");
			}
		}

		/* 卡充值策略 */
		String chargeMoney = this.getChargeMoney();
		if (!StringUtils.isBlank(chargeMoney)) {

			try {
				powerCardJson = MAPPER.readValue(chargeMoney, PowerCardJson.class);
			} catch (Exception e) {
				log.info("卡充值策略json解析失败");
			}
		}

		// 充值开关
		if(sendMoney==null) {
			this.powerCard = 0;
		}else {
			this.powerCard = 1;
		}

		/* 门店策略处理 */
		if (storeList != null) {
			storeList = storeList.replaceAll("\\s+", "");
			storeIdList = Util.json2Object(storeList, new TypeReference<List<Integer>>() {
            }, false);

			if(storeIdList != null && storeIdList.size()>0) {
				/* 门店类型 */
				if (MCARD_STP_BAN.equals(storeIdList.get(0).byteValue()) || MCARD_STP_ALL.equals(storeIdList.get(0).byteValue())) {
					storeListType = storeIdList.get(0).byteValue();
				} else {
					storeListType = MCARD_STP_PART;
				}
			}
		}

		/* 激活需要填写的信息 */
		String activationCfg = getActivationCfg();
		if(null != activationCfg) {
			activationCfgBox = activationCfg.replaceAll("\\s+","").split(",");
		}

		/* 购买类型 */
		if(BUY_BY_CRASH.equals(payType)) {
			payMoney = payFee;
		}else if(BUY_BY_SCORE.equals(payType)) {
			payScore = payFee;
		}

		// 优惠券
		couponIds = Util.stringToList(sendCouponIds);
	}
	private boolean isNotBlank(String val) {
		return !StringUtils.isBlank(val);
	}
}
