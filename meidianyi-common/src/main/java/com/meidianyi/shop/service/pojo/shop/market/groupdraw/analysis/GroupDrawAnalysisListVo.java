package com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis;

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
public class GroupDrawAnalysisListVo {
	/** 订单数 */
	private Integer orderNumber = 0;
	/** 已瓜分积分数 */
	private Integer joinNum = 0;
	/** 成团用户数 */
	private Integer successUserNum = 0;
	/** 拉新用户数 */
	private Integer newUser = 0;

	private String dateTime;
}
