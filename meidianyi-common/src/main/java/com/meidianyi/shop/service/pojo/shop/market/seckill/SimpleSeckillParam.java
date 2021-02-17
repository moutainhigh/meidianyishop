package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-08-12 16:13
 **/
@Data
public class SimpleSeckillParam {
    /**
     * 活动的主键
     */
    @NotNull
    @Min(1)
    private Integer skId;
}
