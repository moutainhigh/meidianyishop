package com.meidianyi.shop.service.pojo.shop.store.service.order;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月18日
 */
@Data
public class ServiceOrderAdminMessageParam {
	@NotNull
	private String     orderSn;
	@NotNull
	private String     adminMessage;
}
