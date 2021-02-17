package com.meidianyi.shop.service.pojo.wxapp.market.bargain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-01-03 17:40
 **/
@Getter
@Setter
public class BargainRecordInfo {
    private Integer    id;
    private Integer    userId;
    private Integer    bargainId;
    private Integer    goodsId;
    private Integer    prdId;
    private BigDecimal goodsPrice;
    private BigDecimal bargainMoney;
    private Integer    userNumber;
    private Byte       status;
    private Byte    isOrdered;
    private String     orderSn;
    private Timestamp createTime;

    private String userAvatar;
    private String goodsName;
    private String goodsImg;
    private BigDecimal prdPrice;
    private String prdDesc;
    private Integer prdNumber;
    /** 1默认规格，0自定义规格（多规格）*/
    private Byte isDefaultProduct;

    private Byte bargainType;
    private Timestamp startTime;
    private Timestamp endTime;
    private BigDecimal expectationPrice;
    private BigDecimal floorPrice;
    private String shareConfig;
    private Integer stock;
    private Timestamp updateTime;
    private Byte needBindMobile;
    private Integer initialSales;
    private Byte freeFreight;

    private String wxOpenid;
    private String username;

    private long remainingTime;
}
