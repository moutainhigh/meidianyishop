package com.meidianyi.shop.service.pojo.shop.recommend;

import com.meidianyi.shop.service.pojo.shop.recommend.collect.JsonCollectBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年11月18日 上午11:46:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendCollectBean {
	/**
	 * 状态 1：插入 2：删除
	 */
	private Integer status;
	private JsonCollectBean bean;
	private Integer shopId;
	
	 private Integer taskJobId;
}
