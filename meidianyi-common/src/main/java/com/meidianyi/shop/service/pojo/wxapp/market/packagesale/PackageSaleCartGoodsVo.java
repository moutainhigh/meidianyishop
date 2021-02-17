package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2020-04-10 14:09
 **/
@Setter
@Getter
public class PackageSaleCartGoodsVo {

    private Integer goodsId;
    private String goodsName;
    private String goodsImg;
    private BigDecimal marketPrice;
    private BigDecimal shopPrice;
    private Integer productId;
    private BigDecimal prdPrice;
    private Integer prdNumber;
    private String prdDesc;
    private String prdImg;

    /** 已选数量 */
    private Integer goodsNumber;

}
