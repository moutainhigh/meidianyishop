package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import lombok.Data;

/**
 * 积分兑换分页查询列表
 * @author liangchen
 * @date 2019年8月14日
 */
@Data
public class IntegralConvertListParam {
	/** 
	  *   活动状态 
	 * 0：全部活动   1：进行中     2：未开始      3：已过期       4：已停用 
	  *   默认：1
	 */
	private Integer actState = IntegralConvertConstant.DOING;
	
	/** 分页信息 */
	private Integer currentPage =1;
	private Integer pageRows =20;
	
}
