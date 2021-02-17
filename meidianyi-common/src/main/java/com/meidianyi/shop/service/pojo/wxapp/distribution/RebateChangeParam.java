package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author 常乐
 * @Date 2020-04-06
 * 分销改价保存接口入参
 */
@Data
public class RebateChangeParam {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 商品ID
     */
    private Integer goodsId;
    /**
     * 规格ID
     */
    private Integer productId;
    /**
     * 建议价格
     */
    private BigDecimal advicePrice;

}
