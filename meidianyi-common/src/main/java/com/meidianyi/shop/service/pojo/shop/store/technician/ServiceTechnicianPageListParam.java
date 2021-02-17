package com.meidianyi.shop.service.pojo.shop.store.technician;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月15日
 *售后分页查询入参
 */
@Data
@NoArgsConstructor
public class ServiceTechnicianPageListParam {
	/** 售后姓名 */
	private String technicianName;
	/** 售后电话 */
	private String technicianMobile;
	/** 售后分组 */
	private Integer groupId;
	/** 店铺ID*/
	@NotNull(message =JsonResultMessage.STORE_STORE_ID_NULL )
	private Integer storeId;
	
	 /**
     * 	分页信息
     */
    private int currentPage = Page.DEFAULT_CURRENT_PAGE;
    private int pageRows = Page.DEFAULT_PAGE_ROWS;
}
