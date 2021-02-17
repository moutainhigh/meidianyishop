package com.meidianyi.shop.service.pojo.shop.member.userimp;

import java.util.List;

import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kangyaxin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetNoticeJsonVo {
	/** 通知说明 */
	private String explain;
	/** 积分 */
	private String score;
	/** 优惠券Id */
	private String mrkingVoucherId;
	private List<CouponView> mrkingVoucherList;
}
