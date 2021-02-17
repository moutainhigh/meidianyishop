package com.meidianyi.shop.service.pojo.wxapp.comment;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * 小程序商品页评价详情
 * @author liangchen
 * @date 2020.03.26
 */
@Data
public class MpGoodsCommentParam {
    /** 商品id */
    private Integer goodsId;
    /** 筛选条件 */
    private Byte type;
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
