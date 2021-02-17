package com.meidianyi.shop.service.pojo.shop.goods.spec;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *  简单的规格信息
 * @author 孔德成
 * @date 2019/12/5 16:07
 */
@Setter
@Getter
public class ProductSmallInfoVo {

    private Integer prdId;

    private Integer goodsId;
    /**
     * 商品名
     */
    private String goodsName;
    /**
     * 规格名
     */
    private String prdDesc;
    /**
     * 商品图片
     */
    private String goodsImg;
    /**
     * 规格数量
     */
    private Integer prdNumber;
    /**
     * 规格价
     */
    private BigDecimal prdPrice;
    /**
     * 是否在售
     */
    private Byte isOnSale;


}
