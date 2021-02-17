package com.meidianyi.shop.service.pojo.shop.market.packagesale;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.service.pojo.shop.order.OrderPageListQueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author huangronggang
 * @date 2019年8月13日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackSaleOrderPageParam {
	/** 活动ID */
	@NotNull
	private Integer activityId;
	/** 商品名称 */
	private String goodsName;
	/** 订单号 */
	private String orderSn;
	/** 订单状态 */
	private Byte[] orderStatus;;
	/** 收货人姓名 */
	private String consignee;
	private String mobile;
	/** 省别码 */
	public Integer provinceCode;
	/** 城市码 */
	public Integer cityCode;
	/** 区域码 */
	public Integer districtCode;
	
	/**
	 * 分页信息
	 */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;

    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
    
    
    public OrderPageListQueryParam convert2OrderParam() {
    	OrderPageListQueryParam orderParam = new OrderPageListQueryParam();
    	orderParam.setActivityId(activityId);
    	orderParam.setGoodsName(goodsName);
    	orderParam.setOrderSn(orderSn);
    	orderParam.setOrderStatus(orderStatus);
    	orderParam.setGoodsType(new Byte[] {BaseConstant.ACTIVITY_TYPE_PACKAGE_SALE});
    	orderParam.setConsignee(consignee);
    	orderParam.setMobile(mobile);
    	orderParam.setProvinceCode(provinceCode);
    	orderParam.setCityCode(cityCode);
    	orderParam.setDistrictCode(districtCode);
    	orderParam.setCurrentPage(currentPage);
    	orderParam.setPageRows(pageRows);
    	return orderParam;
    }
}

