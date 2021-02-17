package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.firstspecial;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 首单特惠规格信息
 * @author 李晓冰
 * @date 2020年01月06日
 */
@Data
public class FirstSpecialPrdMpVo {
    /**规格id*/
    private Integer productId;
    /**首单特惠价格*/
    private BigDecimal firsSpecialPrice;
    /**规格原价*/
    private BigDecimal prdPrice;
}
