package com.meidianyi.shop.service.pojo.shop.member.card.create;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
/**
 * 会员卡续费信息
 * @author 黄壮壮
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRenew {
	/**
	 * 不可续费
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public final static Byte RENEW_CARD_NO = 0;
	/**
	 * 可续费
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public final static Byte RENEW_CARD_OK = 1;
	
	/**
	 * 续费类型： 现金
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public final static Byte RENEW_TYPE_MONEY = 0;
	/**
	 * 续费类型： 积分
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public final static Byte RENEW_TYPE_SCORE = 1;
	/**
	 * 续费时间单位枚举
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public enum DateType{
		day,week,month
	}
	public static void main(String ...args) {
		int[] ids = new int[] {0,1,2};
		for(int index: ids) {
			System.out.println(index+" index is "+DateType.values()[index]);
		}
	}
	/**
	 * 是否可续费
	 */
	private Byte renewMemberCard;
	
	/**
	 * 续费的类型
	 */
	private Byte renewType;
	
	/**
	 * 续费数值
	 */
	private BigDecimal renewNum;
	
	/**
	 * 续费时长
	 */
	private Integer renewTime;
	
	/**
	 * 续费时长单位
	 */
	private DateType renewDateType;
}
