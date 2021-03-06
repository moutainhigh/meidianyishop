/*
 * This file is generated by jOOQ.
 */
package com.meidianyi.shop.common.pojo.shop.table;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;


/**
 * 订单商品表  b2c_order_goods
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Data
public class OrderGoodsDo implements Serializable {

    private static final long serialVersionUID = -761835976;

    private Integer    recId;
    private Integer    mainRecId;
    private Integer    shopId;
    private Integer    orderId;
    private String     orderSn;
    private Integer    goodsId;
    private String     goodsName;
    private String     goodsQualityRatio;
    private String     goodsProductionEnterprise;
    private String     goodsApprovalNumber;
    private String     goodsSn;
    private Integer    productId;
    private String     productSn;
    private Integer    goodsNumber;
    private BigDecimal marketPrice;
    private BigDecimal goodsPrice;
    private String     goodsAttr;
    private Integer    sendNumber;
    private Integer    returnNumber;
    private Byte       isReal;
    private String     goodsAttrId;
    private String     goodsImg;
    private Byte       refundStatus;
    private Byte       commentFlag;
    private Integer    straId;
    private BigDecimal perDiscount;
    private Integer    isGift;
    private String     rGoods;
    private Integer    goodsScore;
    private Integer    goodsGrowth;
    private BigDecimal discountedGoodsPrice;
    private String     discountDetail;
    private Byte       fanliType;
    private BigDecimal canCalculateMoney;
    private BigDecimal fanliMoney;
    private BigDecimal discountedTotalPrice;
    private BigDecimal totalFanliMoney;
    private String     fanliStrategy;
    private BigDecimal fanliPercent;
    private BigDecimal costPrice;
    private Byte       isCardExclusive;
    private String     promoteInfo;
    private Integer    giftId;
    private Byte       isCanReturn;
    private Short      reducePriceNum;
    private Byte       activityType;
    private Integer    activityId;
    private Integer    activityRule;
    private Timestamp  createTime;
    private Timestamp  updateTime;
    private Integer    purchaseId;
    private String     prescriptionOldCode;
    private String     prescriptionCode;
    private String     prescriptionDetailCode;
    private Byte       medicalAuditType;
    private Byte       medicalAuditStatus;
    private Timestamp  auditTime;

}
