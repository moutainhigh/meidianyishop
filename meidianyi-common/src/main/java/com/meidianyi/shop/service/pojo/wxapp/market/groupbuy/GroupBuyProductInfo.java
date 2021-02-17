package com.meidianyi.shop.service.pojo.wxapp.market.groupbuy;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 规格
 * @author 孔德成
 * @date 2019/12/11 10:53
 */
@Getter
@Setter
public class GroupBuyProductInfo {
    private Integer prdId;
    private Integer goodsId;
    private BigDecimal prdMarketPrize;
    private String prdSn;
    private String prdCodes;
    private String prdSpecs;
    private BigDecimal lowShopPrice;
    private String prdImg;
    private Integer prdNumber;
    private BigDecimal prdPrice;
    private Integer stock;
    private String prdName;
    private String specValId;


}
