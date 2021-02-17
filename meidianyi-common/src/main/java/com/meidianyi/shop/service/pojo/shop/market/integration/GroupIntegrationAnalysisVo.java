package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 效果数据的返回
 * 
 * @author zhaojianqiang
 * @time 下午2:14:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupIntegrationAnalysisVo {
	private List<GroupIntegrationAnalysisListVo> list;
	/** 结束时间起始条件 */
	private Timestamp startTime;
	/** 结束时间终止条件 */
	private Timestamp endTime;
	/** 已瓜分积分数 */
	private Integer integrationNum = 0;
	/** 已瓜分积分数 */
	private Integer joinNum = 0;
	/** 成团用户数 */
	private Integer successUserNum = 0;
	/** 拉新用户数 */
	private Integer newUser = 0;
}
