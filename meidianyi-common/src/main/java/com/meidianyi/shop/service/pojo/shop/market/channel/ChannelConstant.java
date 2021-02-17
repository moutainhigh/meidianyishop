package com.meidianyi.shop.service.pojo.shop.market.channel;

/**
 * @author huangronggang
 * @date 2019年8月14日
 * 渠道分析相关常量
 */
public class ChannelConstant {
	/** 来源类型 自定义 */
	public final static Byte SOURCETYPE_CUSTOMIZE =0;
	/** 来源类型 商品 */
	public final static Byte SOURCETYPE_GOODS =1;
	
	/** 用户来源 */
	public final static String INVITESOURCE = "channel";
	
	
	/** 渠道页面数据分析 统计指标  访问次数 */
	public final static Byte ACCESS_TIMES=0;
	/** 渠道页面数据分析 统计指标  访问人数 */
	public final static Byte VISITOR_NUM=1;
	
	/** 访客类型 全部*/
	public final static Byte ALL_VISITOR =0;
	/** 访客类型 新用户 */
	public final static Byte NEW_VISITOR=1;
	/** 访客类型 老用户 */
	public final static Byte OLD_VISITOR=2;
	
	
}

