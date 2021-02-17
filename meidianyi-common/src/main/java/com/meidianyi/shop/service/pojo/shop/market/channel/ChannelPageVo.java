package com.meidianyi.shop.service.pojo.shop.market.channel;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月14日
 */
@Data
@NoArgsConstructor
public class ChannelPageVo {
	/** 渠道页ID*/
	private Integer id;
	/** 源页面 名称 */
	private String pageName;
	/** 原页面ID */
	private Integer pageId;
	/**商品详情页 id */
	private Integer goodsId;
	/** 渠道页名称 */
	private String channelName;
	/**  来源类型 0自定义 1商品 */
	private Byte sourceType;
	/**分享路径 */
	private String share;
	/** 状态 ： 0正常，1废除 */
	private Byte delFlag;
	/** 添加时间 */
	private Timestamp createTime;
	
	/** 拉新用户数 */
	private Integer newUserNum;
	/** 渠道用户订单数 */
	private Integer orderNum;
	/** 昨日访问次数 */
	private Integer yesterdayAccessTimes;
	/** 昨日访问人数 */
	private Integer yesterdayAccessNum;
}

