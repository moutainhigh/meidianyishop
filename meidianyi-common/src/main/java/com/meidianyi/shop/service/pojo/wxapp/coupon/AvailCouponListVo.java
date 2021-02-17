package com.meidianyi.shop.service.pojo.wxapp.coupon;

import com.meidianyi.shop.common.foundation.util.PageResult;

import lombok.Data;

/**
 * 优惠券按状态出参
 * @author 常乐
 * 2019年9月25日
 */
@Data
public class AvailCouponListVo {
	public Integer unusedNum;
	public Integer usedNum;
	public Integer expiredNum;
	public PageResult<AvailCouponVo> couponList;
	
}
