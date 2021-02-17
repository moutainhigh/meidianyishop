package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2019-11-13 13:34
 **/
@Getter
@Setter
public class SecKillPrdMpVo {
    /** 规格ID */
    private Integer productId;

    /** 规格秒杀价 */
    private BigDecimal secKillPrice;

    /** 规格原价，方便计算 */
    private BigDecimal prdPrice;

    /** 规格秒杀库存 */
    private Integer stock;

    /** 规格秒杀总库存 */
    private Integer totalStock;
}