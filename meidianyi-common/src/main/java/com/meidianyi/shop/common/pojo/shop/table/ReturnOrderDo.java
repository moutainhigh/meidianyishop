package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2020/10/15 10:47
 */
@Data
public class ReturnOrderDo {

    private Integer    retId;
    private Integer    orderId;
    private String     orderSn;
    private String     returnOrderSn;
    private Integer    shopId;
    private Integer    userId;
    private Integer    goodsId;
    private Byte       refundStatus;
    private BigDecimal money;
    private BigDecimal shippingFee;
    private Byte       returnType;
    private Byte       reasonType;
    private String     reasonDesc;
    private String     shippingType;
    private String     shippingNo;
    private String     goodsImages;
    private String     voucherImages;
    private String     phone;
    private Timestamp applyTime;
    private Timestamp  applyPassTime;
    private Timestamp  applyNotPassTime;
    private Timestamp  shippingOrRefundTime;
    private Timestamp  refundSuccessTime;
    private Timestamp  refundRefuseTime;
    private Timestamp  refundCancelTime;
    private String     applyNotPassReason;
    private String     refundRefuseReason;
    private String     returnAddress;
    private String     merchantTelephone;
    private String     consignee;
    private String     zipCode;
    private String     currency;
    private Timestamp  createTime;
    private Timestamp  updateTime;
    private Byte       isAutoReturn;
    private Byte       returnSource;
    private Byte       returnSourceType;

    List<ReturnOrderGoodsDo> returnOrderGoodsDoList;
}
