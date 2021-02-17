package com.meidianyi.shop.service.pojo.shop.store.comment;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月18日
 *
 */
@Data
@NoArgsConstructor
public class ServiceCommentPageListParam {
	/** 订单编号 */
	private String orderSn;
	/** 服务名称 */
	private String serviceName;
	/** 门店ID */
	private Integer storeId;
	/** 用户手机号 */
	private String mobile;
	/** 技师名称 */
	private String technicianName;
	
	/** 评价星级 */
	private Byte commstar;
	
	/** 0:未审批,1:审批通过,2:审批未通过 */
	private Byte flag;

	 /**
     * 	分页信息
     */
    private int currentPage = Page.DEFAULT_CURRENT_PAGE;
    private int pageRows = Page.DEFAULT_PAGE_ROWS;
}
