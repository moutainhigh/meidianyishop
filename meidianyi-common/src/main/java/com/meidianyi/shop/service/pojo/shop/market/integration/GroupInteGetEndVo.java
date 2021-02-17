package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 获得已经结束的拼团
 * 
 * @author zhaojianqiang
 * @time 下午6:50:28
 */
@Data
public class GroupInteGetEndVo {
	private Integer id;
	private String name;
	private Short limitAmount;
	private Byte divideType;
	private Integer groupId;
	private Integer canIntegration;
	private Timestamp startTime;
}
