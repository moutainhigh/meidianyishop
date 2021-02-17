package com.meidianyi.shop.service.pojo.wxapp.order;


import com.meidianyi.shop.service.pojo.wxapp.order.base.BaseParam;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 微信小程序获取订单列表
 * @author 王帅
 *
 */
@Getter
@Setter
@ToString
public class OrderListParam extends BaseParam{
	public Integer currentPage;
	public Integer pageRows;
	private Byte type;
	private String search;
	private Byte platform;
	private Integer storeId;
	/**门店后台门店过滤*/
	private List<Integer> storeIds;
}
