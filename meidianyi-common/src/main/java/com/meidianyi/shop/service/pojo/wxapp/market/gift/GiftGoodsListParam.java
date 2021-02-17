package com.meidianyi.shop.service.pojo.wxapp.market.gift;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author: 王兵兵
 * @create: 2020-03-24 16:12
 **/
@Getter
@Setter
public class GiftGoodsListParam {

    /**活动主键*/
    @NotNull
    private Integer giftId;

    private String search;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
