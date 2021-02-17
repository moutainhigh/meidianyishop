package com.meidianyi.shop.service.pojo.shop.store.store;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 赵晓东
 * @description 热销商品列表
 * @create 2020-09-14 15:30
 **/
@Data
public class StoreBestSellersVo {

    private String goodsCommonName;
    private String goodsQualityRatio;
    private String goodsProductionEnterprise;
    private String goodsSn;
    private BigDecimal totalPrice;
    private Integer goodsNumber;

}
