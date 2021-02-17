package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2019-10-28 18:21
 **/
@Data
public class SeckillProductAddParam {
    /**  主键，更新时使用 */
    private Integer skproId;
    @NotNull
    private Integer goodsId;
    @NotNull
    private Integer productId;
    @NotNull
    private BigDecimal secKillPrice;
    @NotNull
    private Integer stock;
}
