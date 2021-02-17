package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 秒杀商品规格信息
 * @author 孔德成
 * @date 2019/11/13 17:14
 */
@Getter
@Setter
public class SeckillProductBo {

    /**
     * 秒杀活动id
     */
    private Integer skId;
    /**
     * 专属会员卡id
     */
    private String cardId;
    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 规格id
     */
    private Integer productId;
    /**
     * 秒杀价格
     */
    private BigDecimal secKillPrice;
}
