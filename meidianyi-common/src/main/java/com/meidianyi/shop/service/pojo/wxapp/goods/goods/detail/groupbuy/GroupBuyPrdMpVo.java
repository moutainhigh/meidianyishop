package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupbuy;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2019年12月23日
 */
@Data
public class GroupBuyPrdMpVo {
    /** 规格id */
    private Integer productId;
    /** 对应规格可拼商品团数量 */
    private Integer stock;
    /** 拼团价格 */
    private BigDecimal groupPrice;
    /** 团长价格 */
    private BigDecimal grouperPrice;
    /** 规格原价 */
    private BigDecimal prdPrice;
}
