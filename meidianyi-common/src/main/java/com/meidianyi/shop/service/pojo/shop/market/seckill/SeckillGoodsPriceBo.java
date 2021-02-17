package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2020-02-26 20:34
 **/
@Setter
@Getter
public class SeckillGoodsPriceBo {
    private Integer goodsId;
    private Integer skId;
    private BigDecimal secKillPrice;
    private Integer stock;
    private Integer totalStock;
}
