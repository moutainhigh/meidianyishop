package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 * @time   下午2:01:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupIntegrationAnalysisParam {
	/** 活动ID */
	@NotNull
	private Integer actId;
	/** 结束时间起始条件 */
	private Timestamp startTime;
	/** 结束时间终止条件 */
	private Timestamp endTime;
}

