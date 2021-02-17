package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品规格
 * @author 孔德成
 * @date 2019/8/29 14:48
 */
@Data
public class GoodsProductVo {

    /**
     * 规格id
     */
    private Integer prdId;
    /**
     * 规格描述
     */
    private String prdDesc;
    /**
     * 规格价格
     */
    private BigDecimal prdPrice;
    /**
     * 当前库存
     */
    private Integer prdNumber;

}
