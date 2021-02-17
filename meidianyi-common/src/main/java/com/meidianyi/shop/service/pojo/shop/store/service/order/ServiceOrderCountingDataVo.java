package com.meidianyi.shop.service.pojo.shop.store.service.order;

import lombok.Data;

/**
 * @author 王兵兵
 *
 * 2019年7月18日
 */
@Data
public class ServiceOrderCountingDataVo {
	private Integer all;
	private Integer waitPay;
	private Integer waitService;
	private Integer canceled;
	private Integer finished;
}
