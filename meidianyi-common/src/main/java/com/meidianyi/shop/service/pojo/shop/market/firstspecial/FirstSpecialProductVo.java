package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author: 王兵兵
 * @create: 2019-08-16 18:03
 **/
@Data
public class FirstSpecialProductVo {

    /** 首单特惠活动b2c_first_special_product表ID */
    private Integer id;

    /** 规格ID */
    private Integer productId;

    /** 规格描述 */
    private String prdDesc;

    /** 规格原价 */
    private BigDecimal originalPrice;

    /** 折后价 */
    private BigDecimal prdPrice;
}
