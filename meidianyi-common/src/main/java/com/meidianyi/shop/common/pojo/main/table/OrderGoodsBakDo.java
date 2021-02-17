package com.meidianyi.shop.common.pojo.main.table;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/8/18 16:13
 */
@Data
public class OrderGoodsBakDo {


    private Long       id;
    private Integer    recId;
    private Integer    mainRecId;
    private Integer    shopId;
    private Integer    orderId;
    private String     orderSn;
    private Integer    goodsId;
    private String     goodsName;
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
    private Byte       medicalAuditType;
    private Byte       medicalAuditStatus;
    private Timestamp auditTime;

}
