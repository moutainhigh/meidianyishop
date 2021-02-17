package com.meidianyi.shop.service.pojo.shop.market.presale;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * 定金膨胀列表查询入参
 *
 * @author 郑保乐
 */
@Getter
@Setter
public class PreSaleListParam extends BasePageParam {


    private String name;
    /** 定金支付开始时间 **/
    private Timestamp preStartTime;
    /** 定金支付结束时间 **/
    private Timestamp preEndTime;
    /** 尾款支付开始时间 **/
    private Timestamp startTime;
    /** 尾款支付结束时间 **/
    private Timestamp endTime;
    /** 状态 **/
    private Byte status;
}
