package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.util.Date;

/**
 * 用户活跃每日数据
 * @author liangchen
 * @date 2019年7月18日
 */
@Data
public class ActiveDailyVo {
    /** 日期 */
	private Date refDate;
    /** 访问会员数 */
	private Integer loginData;
    /** 领券会员数 */
	private Integer couponData;
    /** 加购会员数 */
	private Integer cartData;
    /** 成交会员数 */
	private Integer orderUserData;

}
