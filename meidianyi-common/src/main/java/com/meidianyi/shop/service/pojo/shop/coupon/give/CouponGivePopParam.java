package com.meidianyi.shop.service.pojo.shop.coupon.give;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 优惠券弹窗
 * @author liangchen
 * @date 2019年8月1日
 */
@Getter
@Setter
public class CouponGivePopParam {

	/**
	 * 优惠卷类型  -1全部 0普通 1分裂
	 */
	private Byte type;
	/** 优惠券名称 */
	private String actName;
	/** 分页信息 */
	private int currentPage;
    private int pageRows;
    /**
     * 进行状态 -1全部 0进行中 1未开始
     */
    private Byte status;
}
