package com.meidianyi.shop.service.pojo.shop.member.card;

import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.BUTTON_ON;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.CURRENT_DATE;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_EXPIRED;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MCARD_ET_FIX;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomAction;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomRights;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardRenew;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 黄壮壮
 * @Date: 2019年8月6日
 * @Description: 普通会员卡查询出参
 */
@Getter
@Setter
@Slf4j
public class NormalCardVo extends BaseCardVo {

	/**
	 *	 会员有效期类型 0：固定日期；1：自领取多少内有效；2：永久有效
	 */
	protected Byte expireType;
	
	/**	
	 * 	开始时间
	 */
	protected Timestamp startTime;
	
	/**
	 * 	结束时间 
	 */
	protected Timestamp endTime;
	
	/**
	 * 	自领取之日内多少时间 
	 */
	protected Integer receiveDay;
	
	/** 
	 * 	时间类型 
	 */
	protected Byte dateType;

	/**
	 * 2-会员权益
	 */
	/** 
	 * 	会员折扣开关， 0表示关闭，1表示开启
	 */
	protected Byte powerCount;
	
	/** 
	 * 	会员折扣 值为 0-10之间
	 */
	@JsonProperty("disCount")
	protected BigDecimal discount;
	
	/** 
	 * 	会员专享商品 on表示打开
	 * 	是否专属购买商品 0不是 1是
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Byte payOwnGood;
	protected String powerPayOwnGood;

	/**
	 * 	积分获取开关， 0表示关闭，1表示开启
	 */
	protected Byte powerScore;
	
	/** 
	 * 	购物送积分策略json序列化对象 
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String buyScore;

	/** 
	 * 	卡充值开关 0关闭；1开启
	 */
	protected Byte powerCard;
	/** 
	 * 	卡充值送积分策略json数据
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String chargeMoney;

	/** 
	 *	1:使用中，2:停止使用 3：过期
	 */
	protected Byte flag;
	
	/**
	 * 	包邮信息
	 */
	protected CardFreeship freeship;
	/**
	 * 	续费信息
	 */
	protected CardRenew cardRenew;
	/**
	 * 	自定义权益信息
	 */
	@JsonProperty(value="customRights", access = JsonProperty.Access.READ_ONLY)
	protected CardCustomRights cardCustomRights;
	
	/**
	 *	 自定义激活项
	 */
	protected List<CardCustomAction> customAction;

	/**
	 * 	设置开关及是否过期
	 */
	@Override
	public void changeJsonCfg() {
		log.info("正在执行NormalCardVo的changeJsonCfg.");
		//	会员折扣开关， 0表示关闭，1表示开启 
		powerCount = (byte) (discount == null ? 0 : 1);

		//	会员专享商品 on表示打开 
		powerPayOwnGood = payOwnGood.equals((byte) 0) ? "" : BUTTON_ON;
		//	 积分获取开关， 0表示关闭，1表示开启 
		powerScore = (byte) (buyScore == null ? 0 : 1);
		//	 卡充值开关 0关闭；1开启 
		powerCard = (byte) (chargeMoney == null ? 0 : 1);
		
		//	 处理固定时间段，是否过期 
		if (MCARD_ET_FIX.equals(expireType) && endTime != null) {
			boolean isExpired = endTime.toLocalDateTime().toLocalDate().isBefore(CURRENT_DATE);
			flag = isExpired ? MCARD_EXPIRED : flag;
		}

	}
}
