package com.meidianyi.shop.service.pojo.shop.market.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 营销活动里面用的
 * @author zhaojianqiang
 * @time   下午2:00:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityInfo {
	private Integer inteActivityId;
	/** 消耗积分 */
	private Integer useIntegration;
	/**参与人数 */
	private Integer inteUserSum;
	/** 	团数量 */
	private Integer inteGroupSum;
}
