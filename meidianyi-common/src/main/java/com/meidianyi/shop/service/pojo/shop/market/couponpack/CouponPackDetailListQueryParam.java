package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-21 18:57
 **/
@Data
public class CouponPackDetailListQueryParam {

    /** 优惠券礼包活动的ID */
    @NotNull
    private Integer id;

    private String username;

    private String mobile;

    private Timestamp startTime;

    private Timestamp endTime;

    /** 获取方式，0：现金购买，1：积分购买，2直接领取 */
    private Byte accessMode;

    private String orderSn;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
