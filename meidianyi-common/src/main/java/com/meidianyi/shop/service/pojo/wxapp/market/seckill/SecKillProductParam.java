package com.meidianyi.shop.service.pojo.wxapp.market.seckill;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-11-13 15:06
 **/
@Data
public class SecKillProductParam {
    /** 秒杀活动主键 */
    @NotNull
    private Integer skId;

    /** 秒杀商品ID */
    @NotNull
    private Integer goodsId;

    /** 秒杀规格ID */
    @NotNull
    private Integer productId;

    /** 数量 */
    @NotNull
    private Integer goodsNumber;
}
