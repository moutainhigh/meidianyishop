package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author: 王兵兵
 * @create: 2019-08-07 14:07
 **/
@Data
public class SeckillDetailPageListQueryParam {
    /** 秒杀活动ID */
    @NotNull
    private Integer skId;

    private String mobile;

    private String username;

    /** 筛选购买数量在此之上的*/
    private Short minGoodsAmount;

    /** 筛选购买数量在此之下的*/
    private Short maxGoodsAmount;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;

}
