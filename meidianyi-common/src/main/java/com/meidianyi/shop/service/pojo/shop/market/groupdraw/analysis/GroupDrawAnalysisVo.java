package com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拼团抽奖数据效果
 * @author liangchen
 * @date 2020.01.06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDrawAnalysisVo {
	private List<GroupDrawAnalysisListVo> list;
	/** 结束时间起始条件 */
	private Timestamp startTime;
	/** 结束时间终止条件 */
	private Timestamp endTime;
	/** 付款订单数 */
	private Integer orderNumber = 0;
	/** 已瓜分积分数 */
	private Integer joinNum = 0;
	/** 成团用户数 */
	private Integer successUserNum = 0;
	/** 拉新用户数 */
	private Integer newUser = 0;

}
