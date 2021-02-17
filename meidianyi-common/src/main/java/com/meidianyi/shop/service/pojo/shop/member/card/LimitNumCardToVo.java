package com.meidianyi.shop.service.pojo.shop.member.card;

import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.BUY_BY_CRASH;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.BUY_BY_SCORE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_STP_ALL;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_STP_BAN;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_STP_PART;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomAction;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomRights;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardExchangGoods;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardGive;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardTag;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
/**
* @author 黄壮壮
* @Date: 2019年8月7日
* @Description: 处理策略的限次会员卡出参
*/
@Getter
@Setter
@Slf4j
public class LimitNumCardToVo extends LimitNumCardVo {
	/** 使用须知 */
	private String desc;
	/** 联系方式 */
	private String mobile;
	
	/** 适用商品 0： 不可兑换商品 ；1 ：部分商品；2：全部商品 */
	@JsonProperty("isExchange")
	private Byte isExchang;
	
	
	/**运费策略  0: 免运费 ; 1: 使用商品运费策略*/
	private Byte exchangFreight;
	
	/** 可兑换商品id */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String exchangGoods;
	@JsonProperty("exchangGoods")
	private String[] exchangGoodsArr;
	
	
	/** 激活需要的信息 */
	private String[] activationCfgBox;
	
	
	/** 使用时间 1工作日 2双休 0不限制 */
	private Byte useTime;
	
	/** 购买类型 */
	private Byte payType;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private BigDecimal payFee;
	/**	
	 * 	购买类型0 为 现金购买金额 
	 */
	private BigDecimal payMoney;
	/**	
	 * 	购买类型1 积分购买
	 */
	private BigDecimal payScore;
	
	/**	领取限制 填0为不限制 */
	private Integer limit;
	
	/**
	 * 自定义激活项
	 */
	protected List<CardCustomAction> customAction;
	
	/**
	 * 	同步打标签
	 */
	@JsonProperty("cardTag")
	private CardTag myCardTag;
	
	/**
	 * 	会员卡转赠数据
	 */
	private CardGive cardGive;
	
	/**
	 * 	自定义权益信息
	 */
	@JsonProperty(value="customRights", access = JsonProperty.Access.READ_ONLY)
	protected CardCustomRights cardCustomRights;
	
	/**
	 * 	兑换商品数据信息
	 */
	private CardExchangGoods cardExchangGoods;
	
	
	/**
	 * 处理策略
	 */
	@Override
	public void changeJsonCfg() {
		
		log.info("执行LimitCardToVo的处理策略");
		super.changeJsonCfg();
		
		/** 处理可兑换商品id */
		if(exchangGoods != null) {
			exchangGoods = exchangGoods.replaceAll("\\s+","");
			exchangGoodsArr = exchangGoods.split(",");
		}
		
		/** 门店策略处理 */
		if (storeList != null) {
			storeList = storeList.replaceAll("\\s+", "");
			storeIdList = Util.json2Object(storeList, new TypeReference<List<Integer>>() {
            }, false);
			
			if(storeIdList != null) {
				/** 门店类型 */
				if (MCARD_STP_BAN.equals(storeIdList.get(0).byteValue()) || MCARD_STP_ALL.equals(storeIdList.get(0).byteValue())) {
					storeListType = storeIdList.get(0).byteValue();
				} else {
					storeListType = MCARD_STP_PART;
				}
			}
		}
		
		
		/** 激活需要填写的信息 */
		String activationCfg = getActivationCfg();
		if(null != activationCfg) {
			activationCfgBox = activationCfg.replaceAll("\\s+","").split(",");
		}
		
		/** 购买类型 */
		if(BUY_BY_CRASH.equals(payType)) {
			payMoney = payFee;
		}else if(BUY_BY_SCORE.equals(payType)) {
			payScore = payFee;
		}
		
	}
	
	
}
