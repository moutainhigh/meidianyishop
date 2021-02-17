package com.meidianyi.shop.service.pojo.shop.member.card;

import org.apache.commons.lang3.math.NumberUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
* @author 黄壮壮
* @Date: 2019年8月7日
* @Description: 禁用启动会员卡
*/
@Data
public class PowerCardParam {
	/**
	 * 	直接停用
	 */
	@Setter(AccessLevel.NONE)
	public final static Byte STOP_DIRECT = NumberUtils.BYTE_ZERO;
	
	/**
	 * 	替换为其他会员卡
	 */
	@Setter(AccessLevel.NONE)
	public final static Byte STOP_TO_ANOTHER = NumberUtils.BYTE_ONE;
	/** 
	 * 	会员卡id
	 */
	private Integer id;
	
	/** 
	 * 	是否使用 1:使用中，2:停止使用 
	 */
	private Byte flag;
	
	/**
	 * 	会员卡类型
	 */
	private Byte cardType=CardConstant.MCARD_TP_NORMAL;
	
	/**
	 * 	停用方案 1: 直接停用，2： 置换为其他会员卡
	 */
	private Byte stopPlan = STOP_DIRECT;
	
	/**
	 * 	替换的新卡Id
	 */
	private Integer anotherNewCardId;
}
