package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;



/**
 * @author: 王兵兵
 * @create: 2019-08-16 17:46
 **/
@Data
public class FirstSpecialGoodsVo {

    /** 首单特惠活动b2c_first_special_goods表ID */
    private Integer id;

    private Integer goodsId;

    /** 商品信息 */
    private GoodsSmallVo goodsView;

    /** 折扣 */
    private BigDecimal discount;

    /** 减价 */
    private BigDecimal reducePrice;

    /** 折后价 */
    private BigDecimal goodsPrice;

    /** 规格价 */
    private List<FirstSpecialProductVo> firstSpecialProduct;
}
