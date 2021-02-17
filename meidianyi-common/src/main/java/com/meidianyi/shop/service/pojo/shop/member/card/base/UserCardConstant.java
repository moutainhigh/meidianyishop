package com.meidianyi.shop.service.pojo.shop.member.card.base;
/**
 * 	用户卡状态常量
 * @author 黄壮壮
 *
 */
public class UserCardConstant {
	/*********************************************
	 *  flag
	 **********************************************/
	/**
	 * 	正常使用
	 */
	public static final Byte FLAG_NORMAL = 0;
	/**
	 * 	已废除
	 */
	public static final Byte FLAG_DEL = 1;
	/**
	 * 	转赠中
	 */
	public static final Byte FLAG_CANNOT_USE = 2;
	/**
	 * 	已转赠
	 */
	public static final Byte FLAG_GIVE_FINISHED = 3;
	
	
	/*********************************************
	 *  give_away_status
	 **********************************************/
	/**
	 * 	正常,即没有进行转赠
	 */
	public static final Byte GIVE_AWAY_NORMAL = 0;
	/**
	 * 	转赠中
	 */
	public static final Byte GIVE_AWAY_ING = 1;
	/**
	 * 	转赠成功
	 */
	public static final Byte GIVE_WAY_SUCCESS = 2;
	
	/*********************************************
	 *  card_source
	 **********************************************/
	/**
	 * 	来源： 正常领取
	 */
	 public static final Byte SOURCE_NORMAL = 0;
	 /**
	  *	来源:： 转赠
	  */
	 public static final Byte SOURCE_GIVE_WAY = 2;
	 
}
