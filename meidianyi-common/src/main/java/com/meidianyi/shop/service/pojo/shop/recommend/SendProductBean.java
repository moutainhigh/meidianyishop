package com.meidianyi.shop.service.pojo.shop.recommend;

import com.meidianyi.shop.service.pojo.shop.recommend.product.JsonProductBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年11月18日 上午11:48:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendProductBean {
	private Integer status;
	private JsonProductBean bean;
	private Integer shopId;
	private Integer taskJobId;
}
