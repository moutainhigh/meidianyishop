package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author 常乐
 * @Date 2020-04-11
 */
@Data
public class RebateOrderParam {
    /**
     *  邀请人ID
     */
    private Integer userId;
    /** 订单类型 0：全部订单；1：待返利订单；2：已返利订单*/
    private Integer orderType = 0;
    /**
     * 订单开始时间
     */
    private Timestamp startTime;
    /**
     * 订单结束时间
     */
    private Timestamp endTime;

    private Integer currentPage;

    private Integer pageRows;
}
