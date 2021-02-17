package com.meidianyi.shop.service.pojo.shop.decoration;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author changle
 */
@Data
public class ActivityVo {
	public Integer id;
	public String actName;
	public String type;
	public String actCode;
    /**
     * 优惠券有效期类型
     */
    private Byte    validityType;
    /**
     * 优惠券有效天数
     */
    private Integer    validity;
    /**
     * 优惠券有效小时
     */
    private Integer validityHour;
    /**
     * 优惠券有效分钟数
     */
    private Integer validityMinute;
	public Timestamp startTime;
	public Timestamp endTime;
	private Byte status;

    /**
     * 会员卡名称
     */
    private String cardName;
    /**
     * 过期类型 0:固定日期 1：自领取之日起 2:不过期
     */
    private Byte expireType;
    /**
     * 领取**内
     */
    private Integer receiveDay;
    /**
     * 有效期单位 0:日，1:周 2: 月
     */
    private Byte dateType;
}
