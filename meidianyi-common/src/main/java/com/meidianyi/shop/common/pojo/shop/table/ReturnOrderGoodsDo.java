package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/8/20 18:58
 */
@Data
public class ReturnOrderGoodsDo {

    private Integer    id;
    private Integer    shopId;
    private Integer    recId;
    private Integer    retId;
    private String     orderSn;
    private Integer    goodsId;
    private String     goodsName;
    private Integer    productId;
    private Short      goodsNumber;
    private BigDecimal marketPrice;
    private BigDecimal goodsPrice;
    private String     goodsAttr;
    private Short      sendNumber;
    private BigDecimal returnMoney;
    private BigDecimal discountedGoodsPrice;
    private String     goodsImg;
    private Byte       success;
    private Timestamp createTime;
    private Timestamp  updateTime;

}
