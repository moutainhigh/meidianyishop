package com.meidianyi.shop.service.pojo.shop.decoration;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author 常乐
 * @Date 2020-03-24
 */
@Data
public class CardLinkVo {
    private Integer id;
    /**
     * 会员卡名称
     */
    private String cardName;
    /**
     * 过期类型 0:固定日期 1：自领取之日起 2:不过期
     */
    private Byte expireType;
    /**
     * 有效期开始时间
     */
    private Timestamp startTime;
    /**
     * 有效期结束时间
     */
    private Timestamp endTime;
    /**
     * 领取**内
     */
    private Integer receiveDay;
    /**
     * 有效期单位 0:日，1:周 2: 月
     */
    private Byte dateType;


}
