package com.meidianyi.shop.service.pojo.shop.goods.label;

import lombok.Data;

/**
 * @author 黄荣刚
 * @date 2019年7月4日
 *
 */
@Data
public class GoodsLabelPageListParam {
	 /**
     * 	搜索条件
     */
    private String labelName;
	 /**
     * 	分页信息
     */
    private int currentPage;
    private int pageRows;
}
