package com.meidianyi.shop.service.pojo.wxapp.order.history;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * 购买商品历史记录
 * @author 孔德成
 * @date 2019/11/5 10:18
 */
@Getter
@Setter
public class OrderGoodsHistoryListParam {


    /**
     * 关键字
     */
    private String keyword;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
