package com.meidianyi.shop.service.pojo.shop.member.account;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ISP_BUY;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.SUPPORT_PAY_BY_CASH;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.AVAILABLE_IN_STORE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_TP_LIMIT;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ISE_NON;
import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年10月22日
* @Description: 用户持有会员卡的详细信息
*/
@Data
public class UserCardParam {
	/** user_card bean */
	@JsonAlias({"userId", "user_id"})
	protected Integer userId;
	protected Integer cardId;
	protected Byte userCardFlag;
	protected String cardNo;
	protected Timestamp expireTime;
	protected Byte isDefault;
	protected BigDecimal money;
	protected Integer surplus;
	protected Timestamp activationTime;
	protected Integer exchangSurplus;
	protected Timestamp userCardCreateTime;
	protected Timestamp userCardUpdateTime;
	/**
	 * 	用户卡的快照包邮信息
	 */
	/**   包邮周期类型  */
	protected Byte freeLimit;
	/**   包邮次数 */
	protected Integer freeNum;
	
	/** member_card bean */
	protected Integer mId;
	protected String cardName;
	protected Byte cardType;
	protected Byte bgType;
	protected String bgColor;
	protected String bgImg;
	protected BigDecimal discount;
	protected Integer sorce;
	protected String buyScore;
	protected Byte expireType;
	protected Timestamp startTime;
	protected Timestamp endTime;
	protected Integer receiveDay;
	protected Byte dateType;
	protected Byte activation;
	protected String receiveCode;
	protected String desc;
	protected String mobile;
	protected Timestamp createTime;
	protected Timestamp updateTime;
	protected Byte flag;
	protected Integer sendMoney;
	protected String chargeMoney;
	protected Integer useTime;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String storeList;
	protected Integer count;
	protected Byte delFlag;
	protected String grade;
	protected String gradeCondition;
	protected String activationCfg;
	protected Byte examine;
	protected String discountGoodsId;
	protected String discountCatId;
	protected String discountSortId;
	protected Byte discountIsAll;
	protected Byte isPay;
	protected Byte payType;
	protected BigDecimal payFee;
	protected Integer cash;
	protected Byte payOwnGood;
	protected Byte receiveAction;
	protected Byte isExchang;
	/**	可否在门店使用  0可以 1不可以 */
	protected Byte storeUseSwitch;
	protected String exchangGoods;
	protected Byte exchangFreight;
	protected Integer exchangCount;
	protected Integer stock;
	protected Integer limit;
	protected String discountBrandId;
	/** 0:现金 1：积分 */
    protected Byte renewType;
    /** 续费金额或积分 */
    protected BigDecimal renewNum;
    /** 续费时间 */
    protected Integer renewTime;
    /** 0:日，1:周 2: 月 */
    protected Byte renewDateType;
    /**
     * 优惠券开发 1开启，0关闭
     */
    protected Byte sendCouponSwitch;
    /**
     * 优惠券类型： 0优惠券 1优惠券礼包
     */
    protected Byte sendCouponType;

	/**
	 * 	自定义权益
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Byte customRightsFlag;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String customRights;

	/**
	 * freeship_limit包邮周期类型 -1：不包邮，0:不限制，1：持卡有效期内，2：年，3：季，4：月，5：周，6：日
	 */
	protected Byte freeshipLimit;
	/**
	 * freeship_num 周期内包邮次数
	 */
	protected Integer freeshipNum;
	/**
	 * 	是否和会员卡一起使用0:可以1：不可以
	 */
	protected Byte cannotUseCoupon;
	/**
	 * 0:不可续费，1:可续费
	 */
	protected Byte renewMemberCard;
	
	
	
	/**
	 * 是否支持需要用现金购买
	 * @return
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public boolean isPayByCash(){
		if(isPay==null || payType==null) {
			return false;
		}
		return MCARD_ISP_BUY.equals(isPay) && SUPPORT_PAY_BY_CASH.equals(payType);
	}
	
	public int intCashValue() {
		return payFee.intValue();
	}
	
	public void calcCash() {
		if(isPayByCash()) {
			setCash(intCashValue());
		}
	}
	
	/**   是否可以在门店使用 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public boolean isStoreAvailable() {
		return !StringUtils.isBlank(storeList) && AVAILABLE_IN_STORE.equals(storeUseSwitch);
	}
	
	public boolean hasAvailableExchangGoods() {
		if(cardType==null || isExchang==null) {
			return false;
		}
		return MCARD_TP_LIMIT.equals(cardType) && !MCARD_ISE_NON.equals(isExchang);
	}
	/**
	 * 获取门店列表id List
	 * @return List<Integer>
	 */
	public List<Integer> retrieveStoreList(){
		return changeIncludeCommaStringToIntList(storeList);
	}
	
	/**
	 * 获取使用商品列表id List
	 * @return List<Integer>
	 */
	public List<Integer> retrieveExchangGoods(){
		return changeIncludeCommaStringToIntList(exchangGoods);
	}
	
	private List<Integer> changeIncludeCommaStringToIntList(String idStr) {
		return Util.json2Object(storeList, new TypeReference<List<Integer>>() {
        }, false);
	}
	
	
}
