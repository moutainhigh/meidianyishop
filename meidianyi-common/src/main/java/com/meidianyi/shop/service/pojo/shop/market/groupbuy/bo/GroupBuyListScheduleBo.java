package com.meidianyi.shop.service.pojo.shop.market.groupbuy.bo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-12-05 17:34
 **/
@Data
public class GroupBuyListScheduleBo {
    private Integer activityId;
    private Integer goodsId;
    private String name;
    private Byte isDefault;
    private Short limitAmount;
    private String rewardCouponId;
    private Integer groupId;
    private String orderSn;
    private Timestamp startTime;
}
