package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-04-03
 */
@Data
public class RebateGoodsCfgParam {
    /**
     * 商品ID
     */
    private Integer goodsId;
    /**
     * 返利策略ID
     */
    private Integer rebateId;
}
