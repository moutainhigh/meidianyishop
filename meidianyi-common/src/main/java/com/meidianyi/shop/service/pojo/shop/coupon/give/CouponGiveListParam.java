package com.meidianyi.shop.service.pojo.shop.coupon.give;

import lombok.Data;

/**
 * 定向发放优惠券
 * @author liangchen
 * @date 2019年7月29日
 */

@Data
public class CouponGiveListParam {
	/** 活动名称 */
    private String actName;
	/**
     * 	分页信息
     */
    private Integer currentPage;
    private Integer pageRows;
}
