package com.meidianyi.shop.service.pojo.shop.market.presale;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 定金膨胀活动列表出参
 *
 * @author 郑保乐
 */
@Data
public class PreSaleListVo implements StatusContainer {

    private Integer id;
    /** 名称 **/
    private String presaleName;
    /**
     * 类型，1全款
     */
    private Byte presaleType;
    /**排序*/
    private Integer first;
    /** 定金支付开始时间 **/
    private Timestamp preStartTime;
    /** 定金支付结束时间 **/
    private Timestamp preEndTime;
    /** 尾款支付开始时间 **/
    private Timestamp startTime;
    /** 尾款支付结束时间 **/
    private Timestamp endTime;
    /** 已购商品数量 **/
    private Integer boughtGoodsQuantity;
    /** 订单数 **/
    private Integer orderQuantity;
    /** 已付定金订单数 **/
    private Integer bargainPaidOrderQuantity;
    /** 已付尾款订单数 **/
    private Integer tailPaidOrderQuantity;
    /** 下单用户数 **/
    private Integer orderUserQuantity;
    private Byte status;

    private Byte currentStatus;

    private Timestamp preStartTime2;
    private Timestamp preEndTime2;

    private Byte prePayStep;
}
