package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

/**
 * 积分兑换常量
 * @author liangchen
 * @date 2019年8月19日
 */
public class IntegralConvertConstant {

	/** 
	  *   活动状态 
	 * 0：全部活动   1：进行中     2：未开始      3：已过期       4：已停用 
	  *   默认：1
	 */
	public static final int ALL = 0;
	public static final int DOING = 1;
	public static final int TODO = 2;
	public static final int DID = 3;
	public static final int STOP = 4;

	/** 禁用信息 0：停用  1：启用 */
	public static final Byte BLOCK = 0;
	public static final Byte NOT_BLOCK = 1;
	
	/** 0：未删除 1：删除 */
	public static final Byte NOT_DELETE = 0;
	public static final Byte DELETE = 1;
}
