package com.meidianyi.shop.service.pojo.wxapp.coupon;

import lombok.Data;

/**
 * 用户优惠券列表如参
 * @author 常乐
 * 2019年9月25日
 */
@Data
public class AvailCouponParam {
	/**
	 * 状态值：0未使用；1已使用；2已过期
	 */
	private byte nav;
	/**
	 * 当前页
	 */
	private Integer currentPage;
	/**
	 * 每页显示条数
	 */
	private Integer pageRows;
	
	/**
	 * 当前用户ID
	 */
	private Integer userId;

    /**
     * 卡号
     */
    private String couponSn;

    /**
     * 是否展示不可使用的分裂优惠券
     */
    private Byte isShowEnabledShareSplit;

}
