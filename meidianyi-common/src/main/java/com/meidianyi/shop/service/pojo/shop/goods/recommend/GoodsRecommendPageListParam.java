package com.meidianyi.shop.service.pojo.shop.goods.recommend;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * @author 黄荣刚
 * @date 2019年7月9日
 *
 */
@Data
public class GoodsRecommendPageListParam {
	private String recommendName;
	
	 /**
     * 	分页信息
     */
    private int currentPage = Page.DEFAULT_CURRENT_PAGE;
    private int pageRows = Page.DEFAULT_PAGE_ROWS;
}
