package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.util.List;

/**
 * 用户活跃最终出参
 * @author liangchen
 * @date 2019年7月18日
 */
@Data
public class ActiveTotalVo {
    /** 每日数据 */
    List<ActiveDailyVo> activeDailyVo;
    /** 访问会员数 */
	private Integer loginData;
    /** 领券会员数 */
	private Integer couponData;
    /** 加购会员数 */
	private Integer cartData;
    /** 成交会员数 */
	private Integer orderUserData;
    /** 累计用户数 */
	private Integer userData;
	/** 访问会员数占比 */
    private Double loginDataRate;
    /** 领券会员数占比 */
    private Double couponDataRate;
    /** 加购会员数占比 */
    private Double cartDataRate;
    /** 成交会员数占比 */
    private Double orderUserDataRate;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
}
