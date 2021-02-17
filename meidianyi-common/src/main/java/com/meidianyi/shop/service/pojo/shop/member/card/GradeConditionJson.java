package com.meidianyi.shop.service.pojo.shop.member.card;

import java.math.BigDecimal;

import lombok.Data;
/**
 * 
 * @author 黄壮壮
 * @Date: 2019年8月1日
 * @Description: 会员卡升级策略json对象
 */
@Data
public class GradeConditionJson {
	
	/** 累积积分达到多少 */
	private BigDecimal gradeScore;
	/** 累积消费总额达到多少 */
	private BigDecimal gradeMoney;
	
}
