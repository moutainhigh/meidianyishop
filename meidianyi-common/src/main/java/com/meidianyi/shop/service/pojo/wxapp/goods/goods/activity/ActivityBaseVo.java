package com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2019年10月23日
 */
@Deprecated
@Data
public class ActivityBaseVo {
    /**活动在各自活动表内的id值*/
    Integer activityId;
    /**大部分情况下是商品的goodsType*/
    Byte activityType;
    /**活动开始时间*/
    private Timestamp startTime;
    /**活动结束时间*/
    private Timestamp endTime;
    /**参与活动的商品的活动价格*/
    private BigDecimal activityPrice;
}
