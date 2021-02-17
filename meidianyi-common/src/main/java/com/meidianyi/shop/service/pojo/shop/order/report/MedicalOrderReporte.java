package com.meidianyi.shop.service.pojo.shop.order.report;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/8/3 17:17
 */
public class MedicalOrderReporte {
    private Timestamp createTime;
    private String time;

    private Integer orderNumber;

    private BigDecimal orderAmount;
    private BigDecimal orderAvg;

    private Integer orderMedicalNumber;

    private BigDecimal orderMedicalAmount;

    private Integer returnNumber;

    private BigDecimal returnAmount;
}
