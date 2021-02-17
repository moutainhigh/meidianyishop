package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * 返利商品统计入参
 * @author 常乐
 * 2019年8月10日
 */
@Data
public class RebateGoodsParam {
	private Integer goodsSort;
	private String goodsName;
    private Integer startNum;
    private Integer endNum;
	
	/**
     * 	分页信息
     */
	private int currentPage;
	private int pageRows;
}
