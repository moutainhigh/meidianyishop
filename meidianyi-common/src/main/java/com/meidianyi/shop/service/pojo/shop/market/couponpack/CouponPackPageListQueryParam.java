package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.meidianyi.shop.common.foundation.util.Page;

/**
 * @author: 王兵兵
 * @create: 2019-08-20 14:15
 **/
@Data
public class CouponPackPageListQueryParam {

    private String actName;

    private String packName;

    /**
     * 领取方式过滤 ：-1全部，0：现金购买，1：积分购买，2直接领取
     */
    private Byte accessMode;

    /**
     * 活动状态过滤 ：0全部，1进行中，2未开始，3已过期，4已停用
     */
    @Max(4)
    @Min(0)
    private Byte state = (byte)1;

    /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
