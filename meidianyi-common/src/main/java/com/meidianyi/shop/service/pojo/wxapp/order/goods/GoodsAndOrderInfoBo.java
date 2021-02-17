package com.meidianyi.shop.service.pojo.wxapp.order.goods;

import lombok.Getter;
import lombok.Setter;

/**
 * 商品信息和订单信息
 * @author 孔德成
 * @date 2020/4/26
 */
@Getter
@Setter
public class GoodsAndOrderInfoBo {
    /**
     * 商品id
     */
    private Integer goodsId;
    /**
     * 规格id
     */
    private Integer productId;
    /**
     * 是否是赠品
     */
    private Byte isGift;
    /**
     * 订单商品数量
     */
    private Integer goodsNumber;
    /**
     * 规格库存
     */
    private Integer prdNumber;
    /**
     * 是否在售 ，1在售，0下架
     */
    private Byte isOnSale;
    /**
     * 是否删除
     */
    private Byte delFlag;
}
