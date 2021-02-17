package com.meidianyi.shop.service.pojo.shop.member.card;

import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.CURRENT_DATE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_EXPIRED;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ET_FIX;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 黄壮壮
 * @Date: 2019年8月6日
 * @Description: 限次会员卡出参
 */
@Getter
@Setter
@Slf4j
public class LimitNumCardVo extends BaseCardVo {

	/**
	 * 会员有效期类型 0：固定日期；1：自领取多少内有效；2：永久有效
	 */
	private Byte expireType;
	/** 开始时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	/** 自领取之日内多少时间 */
	private Integer receiveDay;
	/** 时间类型 */
	private Byte dateType;

	/** 可以领取的总次数 */
	private Integer stock;
	/** 已经领取的次数 */
	private Integer hasSend;

	/** 商品兑换次数 */
	private Integer exchangCount;

	/** 门店兑换次数 */
	private Integer count;

	/** 1:使用中，2:停止使用 3：过期 */
	private Byte flag;

	@Override
	public void changeJsonCfg() {

		log.info("执行LimitCardToVo的处理策略");
		/** 处理固定时间段，是否过期 */
		if (MCARD_ET_FIX.equals(expireType) && endTime != null) {
			boolean isExpired = endTime.toLocalDateTime().toLocalDate().isBefore(CURRENT_DATE);
			flag = isExpired ? MCARD_EXPIRED : flag;
		}
	}
}
