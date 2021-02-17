package com.meidianyi.shop.service.pojo.shop.member.account;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @author 黄壮壮
 * @Date:  2019年8月23日
 * @Description: 积分记录-入参类
 */
@Data
public class ScoreParam {
	/** 备注Id  {@link com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate}*/
	private Integer remarkCode;
	
	/** 备注数据 */
	private String remarkData;
	/** -描述 */
	private String desc;
	/** 关联其他属性 */
	private String identityId;
	/** 商品Id */
	private Integer goodsId;
	/** -需要更新的用户id */
	private Integer userId;
	
	/** -积分变动数额  区分正负数 */
	private Integer score;
	
	/** -当前积分 -当批量操作时，为所选用户中的最低积分*/
	private Integer scoreDis;
	
	/** -订单编号 */
	private String orderSn; 
	
	/** -积分状态 {@link com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant} */
	private Byte scoreStatus;
	
	/**	是否退款  0否，1是 */
	private Byte isFromRefund;
	/**	是否过期 0否，1是	*/
	private Byte isFromOverdue;
	/** 是否来自crm 0否，1是 */ 
	private Byte isFromCrm;
	/** 积分来源 */
	private Integer changeWay;
	/** 积分比例 */
	private Integer scoreProportion;
	
	/** 过期时间 */
	private Timestamp expiredTime;
	
	
	
	
	
}