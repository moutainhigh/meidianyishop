package com.meidianyi.shop.service.pojo.shop.market.presale;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 定金膨胀活动订单出参
 *
 * @author 郑保乐
 */
@Data
public class DetailListVo {

    /** 订单id **/
    private Integer orderId;
    /** 订单流水号 **/
    private String orderSn;
    /** 商品名 **/
    private String goodsName;
    /** 商品图片 **/
    private String goodsImg;
    /** 用户id **/
    private Integer userId;
    /** 手机号 **/
    private String mobile;
    /** 收货人姓名 **/
    private String consigneeRealName;
    /** 收货人姓名 **/
    private Timestamp createTime;
    /** 订单状态 **/
    private Byte orderStatus;
    /** 订单状态名 **/
    private String orderStatusName;
    /** 商品数量 **/
    private Integer goodsAmount;
    /** 订单总金额 **/
    private BigDecimal orderAmount;
}
