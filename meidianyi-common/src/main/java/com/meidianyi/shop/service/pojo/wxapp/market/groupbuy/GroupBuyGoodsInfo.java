package com.meidianyi.shop.service.pojo.wxapp.market.groupbuy;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 拼团商品信息
 * @author 孔德成
 * @date 2019/12/10 18:06
 */
@Getter
@Setter
public class GroupBuyGoodsInfo {

    private Integer goodsId;
    private String goodsImg;
    private String goodsName;
    private Integer goodsNumber;
    /**
     * 商品价格
     */
    private BigDecimal shopPrice;
    /**
     * 拼团价格区间
     */
    private BigDecimal maxGroupBuyPrice;
    /**
     * 拼团最低价
     */
    private BigDecimal minGroupBuyPrice;
    /**
     * 拼团库存
     */
    private Integer groupBuygoodsNum;




}
