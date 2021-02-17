package com.meidianyi.shop.service.pojo.wxapp.goods.recommend;

import lombok.Data;

/**
 * 商品表中的各项id
 * @author liangchen
 * @date 2019.12.25
 */
@Data
public class RecSource {
    /** 商品id */
    private Integer goodsId;
    /** 商家分类id */
    private Integer sortId;
    /** 品牌id */
    private Integer brandId;
    /** 平台分类id */
    private Integer catId;
    /** 推荐数 */
    private Integer remainderNumber;
}
