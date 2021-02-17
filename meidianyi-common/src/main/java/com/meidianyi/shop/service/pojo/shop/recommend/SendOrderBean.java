package com.meidianyi.shop.service.pojo.shop.recommend;

import com.meidianyi.shop.service.pojo.shop.recommend.order.JsonRootBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年11月18日 上午11:48:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOrderBean {
	/**
	 * 1：添加；2更新
	 */
	private Integer status;
	private JsonRootBean bean;
	private Integer shopId;
	private Integer taskJobId;
}
