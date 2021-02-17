package com.meidianyi.shop.service.pojo.shop.market.givegift.record;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2019/9/26 17:43
 */
@Data
public class GiftRecordGoodsVo {

    /**
     * 商品id
     */
    private Integer goodsId;
    private String goodsSn;
    private String goodsName;
    private BigDecimal goodsPrice;
    private Integer goodsNumber;
}
