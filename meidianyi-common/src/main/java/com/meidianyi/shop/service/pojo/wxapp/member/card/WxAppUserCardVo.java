package com.meidianyi.shop.service.pojo.wxapp.member.card;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;

import lombok.Getter;
import lombok.Setter;

/**
* @author 黄壮壮
* @Date: 2019年11月20日
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
	/**   是否过期 */
	protected Byte expire;
	/**   是否可续费 */
	protected Byte renewal;
	protected LocalDate startDate;
	protected LocalDate endDate;
	protected String avatar;
	/**   使用商品列表 */
	protected List<GoodsSmallVo> goodsList;
	/**   门店信息 */
	protected List<StoreBasicVo> storeInfoList;
	/**   累积积分 */
	protected Integer cumulativeScore;
	/**   累积消费金额 */
	protected BigDecimal cumulativeConsumptionAmounts;
	/**   审核状态 */
	protected Byte cardVerifyStatus;
	public void calcCardIsExpired(){
		if(isExpire()) {
			this.expire = ALREADY_EXPIRED;
		}
		this.expire = NOT_EXPIRED;
	}
	public void calcRenewal() {
		if(isPermanent()) {
			setRenewal(NON_RENEWAL);
		}
		setRenewal(AVAILABLE_RENEWAL);
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
		//   means endless time,maximum time
		if(!hasExpireTime() && isPermanent()) {
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
