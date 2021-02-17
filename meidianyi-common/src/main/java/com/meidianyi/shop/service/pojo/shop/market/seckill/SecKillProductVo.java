package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2019-08-06 17:06
 **/
@Data
public class SecKillProductVo {
    /** 秒杀商品规格id */
    private Integer skproId;

    /** 秒杀商品规格id */
    private Integer goodsId;

    /** 规格ID */
    private Integer productId;

    /** 规格描述 */
    private String prdDesc;

    /** 规格价 */
    private BigDecimal prdPrice;

    /**
     * 规格库存
     */
    private Integer prdNumber;

    /**
     * 秒杀价
     */
    private BigDecimal secKillPrice;

    /**
     * 秒杀库存
     */
    private Integer totalStock;

    /**
     * 当前秒杀剩余库存
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer saleNum;
}
