package com.meidianyi.shop.service.pojo.shop.market.reduceprice;

import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;



/**
 * @author: 王兵兵
 * @create: 2019-08-14 17:46
 **/
@Data
public class ReducePriceGoodsVo {

    /** 限时降价活动b2c_reduce_price_goods表ID */
    private Integer id;

    private Integer goodsId;

    /** 商品信息 */
    private GoodsView goodsView;

    /** 折扣 */
    private BigDecimal discount;

    /** 减价 */
    private BigDecimal reducePrice;

    /** 折后价 */
    private BigDecimal goodsPrice;

    /** 规格价 */
    private List<ReducePriceProductVo> reducePriceProduct;
}
