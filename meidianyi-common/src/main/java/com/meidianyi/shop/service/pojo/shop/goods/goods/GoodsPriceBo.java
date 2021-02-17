package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 考虑限时降价、首单特惠、等级会员价三种情况下，得出的商品价格
 * @author: 王兵兵
 * @create: 2020-02-19 10:54
 **/
@Getter
@Setter
public class GoodsPriceBo {
    /**
     * 取价结果类型，0商品原价，1等级会员价，2限时降价，3首单特惠价
     */
    private Byte goodsPriceAction;

    private BigDecimal goodsPrice;

    /**
     * 最大的一个规格价
     */
    private BigDecimal maxPrice;

    private BigDecimal marketPrice;
    private Byte isCardExclusive;

    /**
     * 限时降价、首单特惠活动的限购
     */
    private Integer limitAmount;
}
