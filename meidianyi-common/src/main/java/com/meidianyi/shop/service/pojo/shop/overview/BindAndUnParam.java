package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年8月29日 下午2:20:56
 */
@Data
public class BindAndUnParam {
	/**
	 * 操作类型
	 */
	private String act;
	/**
	 * 子账户id
	 */
	private Integer accountId=0;
}
