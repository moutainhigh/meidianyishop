package com.meidianyi.shop.service.pojo.shop.coupon;

import lombok.Getter;
import lombok.Setter;

/**
 * 优惠券列表入参
 * @author 常乐
 * 2019年7月16日
 */
@Getter
@Setter
public class CouponListParam {
    /**状态*/
	private Integer nav;
	/**优惠券名称*/
	private String actName;
	/**优惠券类型*/
	private Byte couponType;

	/**
     * 	分页信息
     */
    private int currentPage;
    private int pageRows;
}
