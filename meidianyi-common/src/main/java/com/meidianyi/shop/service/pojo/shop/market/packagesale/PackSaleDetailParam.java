package com.meidianyi.shop.service.pojo.shop.market.packagesale;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.service.pojo.shop.order.OrderPageListQueryParam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月13日
 * 一口价活动明细 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackSaleDetailParam {
	/** 活动ID */
	@NotNull
	private Integer activityId;
	/** 用户名 */
	private String userName;
	/** 手机号 */
	private String mobile;
	/** 订单号 */
	private String orderSn;
	
	/**
	 * 分页信息
	 */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;

    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
	
	public OrderPageListQueryParam convert2OrderParam() {
		OrderPageListQueryParam orderParam = new OrderPageListQueryParam();
		orderParam.setActivityId(activityId);
		orderParam.setUserName(userName);
		orderParam.setMobile(mobile);
		orderParam.setOrderSn(orderSn);
		orderParam.setGoodsType(new Byte[] {BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE});
		orderParam.setCurrentPage(currentPage);
		orderParam.setPageRows(pageRows);
		return orderParam;
	}
	
}

