package com.meidianyi.shop.service.pojo.shop.goods.deliver;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;


/**
 * 运费模版分页信息
 * @author liangchen
 * @date  2019年7月11日
 */

@Data
public class GoodsDeliverPageListParam {
    /** 标识 0:普通运费模板,1:重量运费模板 */
    @NotNull
    private Byte flag;
	/** 分页信息 */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
