package com.meidianyi.shop.service.pojo.shop.member.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomRights;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;
import com.meidianyi.shop.service.pojo.wxapp.card.vo.CardExchangTipVo;
import com.meidianyi.shop.service.pojo.wxapp.card.vo.CardGiveVo;

import lombok.Getter;
import lombok.Setter;

/**
* @author 黄壮壮
* @Date: 2019年10月28日
* @Description:
*/
@Getter
@Setter
public class WxAppUserCardVo extends UserCardParam {
	final static Byte ALREADY_EXPIRED = 1;
	final static Byte NOT_EXPIRED = 0;
	final static Byte PERMANENT = 2;
	final static Byte NON_RENEWAL = 0;
	final static Byte AVAILABLE_RENEWAL = 1;
	/**
	 * 	 过期状态
	 */
	private Integer status;
	/**
	 * 	 是否过期
	 */
	protected Byte expire;
	/**
	 * 	 是否可续费
	 */
	protected Byte renewal;
	protected LocalDate startDate;
	protected LocalDate endDate;
	protected String avatar;
	protected String qrCode;
	protected NextGradeCardVo nextGradeCard;
	protected WxAppCardExamineVo isExamine;
	/**
	 * freeshipDesc 包邮信息描述
	 */
	protected String freeshipDesc;
	/**
	 * 	自定义权益
	 */
	@JsonProperty("customRights")
	protected CardCustomRights cardCustomRights;

	/**
	 * 	 使用商品列表
	 */
	protected List<GoodsSmallVo> goodsList;
	/**
	 * 	 门店id
	 */
	@JsonProperty("storeList")
	protected List<Integer> storeIdList;
	/**
	 * 	 门店信息
	 */
	protected List<StoreBasicVo> storeInfoList;
	/**
	 * 	 累积积分
	 */
	protected Integer cumulativeScore;
	/**
	 * 	 累积消费金额
	 */
	protected BigDecimal cumulativeConsumptionAmounts;
	/**
	 * 	 审核状态
	 */
	protected Byte cardVerifyStatus;
	/**
	 * 	用户卡的转赠数据
	 */
	protected CardGiveVo cardGive;

	/**
	 *  优惠券列表
	 */
	private List<UserCardCoupon> coupons;
	/**
	 *  优惠券礼包
	 */
	private List<UserCardCouponPack> couponPack;
	/**
	 * 兑换商品提示
	 */
	private CardExchangTipVo cardExchangTip;
	public void calcCardIsExpired(){
		if(isExpire()) {
			this.expire = ALREADY_EXPIRED;
		}else {
			this.expire = NOT_EXPIRED;
		}
	}
	public void calcRenewal() {
		setRenewal(renewMemberCard);
	}
	public void calcUsageTime() {
		if(getUserCardCreateTime()!=null) {
			setStartDate(getUserCardCreateTime().toLocalDateTime().toLocalDate());
		}
		if(getExpireTime()!=null) {
			setEndDate(getExpireTime().toLocalDateTime().toLocalDate());
		}
	}
	private boolean isExpire() {
		// means endless time,maximum time
		if(!hasExpireTime() && isPermanent()) {
			return false;
		}
		if(getExpireTime()==null) {
			return false;
		}
		return getExpireTime().before(DateUtils.getLocalDateTime());

	}
	private boolean hasExpireTime() {
		return getExpireTime() != null;
	}

	private boolean isPermanent() {
		return PERMANENT.equals(getExpireType());
	}
}
