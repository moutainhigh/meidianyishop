package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.util.Page;

import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-21 16:02
 **/
@Data
public class CouponPackOrderListQueryParam {

    /** 优惠券礼包活动的ID */
    @NotNull
    private Integer id;

    private String orderSn;

    /** 下单用户信息：昵称或手机号 */
    private String userInfo;

    /** 下单时间 */
    private Timestamp startTime;

    private Timestamp endTime;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;

}
