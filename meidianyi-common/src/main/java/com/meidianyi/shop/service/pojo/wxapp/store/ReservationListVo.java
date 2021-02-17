package com.meidianyi.shop.service.pojo.wxapp.store;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liufei
 * @date 11/25/19
 */
@Data
public class ReservationListVo {
    private Integer orderId;
    private Integer userId;
    private String orderSn;
    /**   门店信息 */
    private Integer storeId;
    private String storeName;
    private String storeImgs;
    private String storeImg;
    private String mobile;

    /**   服务信息 */
    private Integer serviceId;
    private String serviceName;
    private String serviceImg;
    /**   价格 */
    private String servicePrice;
    /**   定金 */
    private String serviceSubsist;

    /**   订单信息 订单状态 0：待服务，1：已取消，2：已完成 */
    private Byte orderStatus;
    private String orderStatusName;
    private String serviceDate;
    private String servicePeriod;
    /**   实付款 */
    private BigDecimal moneyPaid;
    /**   是否已评价（1：已评价，0：未评价） */
    private Byte flag;
}
