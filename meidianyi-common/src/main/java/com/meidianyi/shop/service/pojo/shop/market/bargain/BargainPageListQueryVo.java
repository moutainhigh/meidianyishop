package com.meidianyi.shop.service.pojo.shop.market.bargain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 王兵兵
 *
 * 2019年7月24日
 */
@Data
public class BargainPageListQueryVo {

	/**
	 * 砍价活动主键 
	 */
	private Integer id;
	
	private String bargainName;
	
	/**
	 * 砍价类型0定人1任意价
	 */
	private Byte bargainType;
	
	private Timestamp startTime;
	private Timestamp endTime;
	
	/**
	 * 停用启用的状态：1可用，0停用 
	 */
	private Byte status;

	/**
	 * 砍价活动库存 
	 */
	private Integer stock;
	/**
	 * 成功数量 
	 */
	private Integer successNumber;
	/**
	 * 发起砍价人数 
	 */
	private Integer bargainUserNumber;

    /**
     * 当前活动状态：1进行中，2未开始，3已结束，4已停用
     */
    private Byte currentState;

    private Integer first;
}
