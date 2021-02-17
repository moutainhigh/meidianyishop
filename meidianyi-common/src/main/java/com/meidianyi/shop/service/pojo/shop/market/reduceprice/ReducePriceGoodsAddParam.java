package com.meidianyi.shop.service.pojo.shop.market.reduceprice;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-08-13 18:01
 **/
@Data
public class ReducePriceGoodsAddParam {

    /**
     * 对应表的主键
     */
    private Integer id;

    /**
     * 商品主键
     */
    @NotNull
    private Integer goodsId;

    /**
     * 打几折
     */
    @NotNull
    private BigDecimal discount;

    /**
     * 减多少钱
     */
    @NotNull
    private BigDecimal reducePrice;

    /** 折后价格 */
    @NotNull
    private BigDecimal goodsPrice;

    /**
     * 改价的规格数组
     */
    private List<ReducePriceGoodsProductAddParam> reducePriceProduct;
}
