package com.meidianyi.shop.service.pojo.shop.store.service.order;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author 王兵兵
 *
 * 2019年7月17日
 */
@Data
public class ServiceOrderListQueryParam {

    /**
     * 前台传入的控制排序方向
     */
    public static final String ASC="asc";
    public static final String DESC="desc";
    /**
     * 	待排序字段
     */
    public static final String CREATE_TIME="createTime";
    public static final String SERVICE_DATE="serviceDate";
	
	@NotNull
	private Integer storeId;
	
	/**
	 *订单状态 0：待服务，1：已取消，2：已完成,3待付款 
	 */
	private Byte orderStatus;
	
	private String mobile;
	
	private String serviceDateStart;
	
	private String serviceDateEnd;
	
	private String technicianName;
	
	/**
	 * 预约人姓名或服务名称
	 */
	private String keywords;

    /**
     * 排序依据列，createTime预约提交时间，serviceDate预约到店时间，默认createTime
     */
	private String orderField;
    /**
     * 排序规则，desc倒叙，asc正序，默认desc
     */
	private String orderDirection;
	
	/**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
