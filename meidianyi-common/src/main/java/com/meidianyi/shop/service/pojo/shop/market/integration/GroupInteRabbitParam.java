package com.meidianyi.shop.service.pojo.shop.market.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 队列中用
 * 
 * @author zhaojianqiang
 * @time 下午2:13:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupInteRabbitParam {
	private Integer groupId;
	private Integer pinInteId;
	private Integer shopId;
	private Integer taskJobId;
}
