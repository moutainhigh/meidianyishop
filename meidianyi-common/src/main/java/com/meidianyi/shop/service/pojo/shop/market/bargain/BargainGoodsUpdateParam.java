package com.meidianyi.shop.service.pojo.shop.market.bargain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2020-03-27 11:31
 **/
@Setter
@Getter
public class BargainGoodsUpdateParam {
    private Integer id;
    private Integer goodsId;
    /** 指定金额结算模式的砍价底价 或 砍到任意金额结算模式的结算金额上限 */
    private BigDecimal expectationPrice;
    /** 任意金额结算模式的结算金额底价 */
    private BigDecimal floorPrice;
    private Integer stock;
}
