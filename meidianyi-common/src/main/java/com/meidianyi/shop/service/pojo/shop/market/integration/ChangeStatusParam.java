package com.meidianyi.shop.service.pojo.shop.market.integration;

import lombok.Data;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月6日
 * 停用和启用瓜分积分活动
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusParam {
	/** 活动ID*/
	@NotNull
	private Integer id;
	/** 状态值 */
	@NotNull
	private Byte status = GroupIntegrationDefineEnums.Status.NORMAL.value();
}

