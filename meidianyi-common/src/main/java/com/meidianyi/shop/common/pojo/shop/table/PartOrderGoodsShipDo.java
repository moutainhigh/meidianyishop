package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 *  订单快递
 * @author 孔德成
 * @date 2020/9/21 15:09
 */
@Data
public class PartOrderGoodsShipDo {
    private Integer   recId;
    private Integer   shopId;
    private Integer   orderGoodsId;
    private String    orderSn;
    private String    batchNo;
    private Integer   goodsId;
    private String    goodsName;
    private Integer   productId;
    private Short     sendNumber;
    private String    goodsAttr;
    private Byte      shippingType;
    private String    shippingNo;
    private String    shippingName;
    private Byte      shippingId;
    private Timestamp shippingTime;
    private Integer   shippingAccountId;
    private Byte      shippingPlatform;
    private String    shippingMobile;
    private Integer   shippingUserId;
    private Timestamp confirmTime;
    private Timestamp createTime;
    private Integer   confirmAccountId;
    private Byte      confirmPlatform;
    private String    confirmMobile;
    private Integer   confirmUserId;
    private Timestamp updateTime;
}
