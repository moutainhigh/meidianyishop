package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月7日
 * 分页查询参团明细查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupIntegrationListDetailParam {
	/** 活动ID 必选*/
	@NotNull
	private Integer actId;
	/** 用户手机号 */
	private String mobile;
	
	private String username;
	/** 参团时间 起始查询条件 */
	private Timestamp startTime;
	/** 参团时间 终止查询条件 */
	private Timestamp endTime;
	/** 是否为团长 */
	private Byte isGrouper;
	/** 邀请人数 */
	private Short inviteNum;
	/** 瓜分积分 最少查询条件 */
	private Integer minIntegration;
	/** 瓜分积分 最多查询条件*/
	private Integer maxIntegration;
	/** 是否为新人 */
	private Byte isNew;
	/** 团ID*/
	private Integer groupId;
	
	/**
     * 	分页信息
     */
    private int currentPage = Page.DEFAULT_CURRENT_PAGE;
    private int pageRows = Page.DEFAULT_PAGE_ROWS;
}

