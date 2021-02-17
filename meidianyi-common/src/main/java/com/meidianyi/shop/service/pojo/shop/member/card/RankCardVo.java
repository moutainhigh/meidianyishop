package com.meidianyi.shop.service.pojo.shop.member.card;

import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.BUTTON_ON;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.MAPPER;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
* @author 黄壮壮
* @Date: 2019年8月6日
* @Description:
*/
@Getter
@Setter
@Slf4j
public class RankCardVo extends BaseCardVo {
	
	/**
	 * 等级会员卡
	 */ 
	/** 开始时间 */
	protected Timestamp startTime;
	/** 结束时间 */
	protected Timestamp endTime;
	
	/** 会员卡等级 */
	protected String grade;
	/** 等级卡升级策略 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String gradeCondition;
	protected GradeConditionJson gradeConditionJson;
	
	
	/**
	 * 2-会员权益
	 */
	/** 会员折扣开关， 0表示关闭，1表示开启 */
	protected Byte powerCount;
	/** 会员折扣 值为 0-10之间 */
	@JsonProperty("disCount")
	protected BigDecimal discount;
	
	/** 会员专享商品 on表示打开 */
	/** 是否专属购买商品 0不是 1是 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Byte payOwnGood;
	protected String powerPayOwnGood;
    /**
     * 专属商品数量
     */
	protected Integer payOwnGoodNum;
	
	/**
	 * 积分获取开关， 0表示关闭，1表示开启
	 */
	protected Byte powerScore;
	/** 购物送积分策略json序列化对象 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String buyScore;
	
	
	/** 1:使用中，2:停止使用  */
	protected Byte flag;
	
	@Override
	public void changeJsonCfg() {
		
		/** 会员折扣开关， 0表示关闭，1表示开启 */
		powerCount = (byte) (discount==null?0:1);
		
		/** 会员专享商品 on表示打开 */
		powerPayOwnGood = payOwnGood.equals((byte)0) ?"":BUTTON_ON;
		
		/** 积分获取开关， 0表示关闭，1表示开启 */
		powerScore  = (byte) (buyScore == null ? 0:1);
		
		/**
		 * 等级会员卡升级策略
		 */
		/** 等级卡升级策略 */
		if(!StringUtils.isBlank(gradeCondition)) {
			try {
				gradeConditionJson = MAPPER.readValue(gradeCondition, GradeConditionJson.class);
			} catch (Exception e) {
				log.info("等级卡升级策略json解析失败");
			} 
		}
	}

}
