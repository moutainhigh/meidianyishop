package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

import java.util.List;

/**
 * @author 孔德成
 * @date 2019/8/29 14:22
 */
@Data
public class GoodsIdParams {

    /**
     * 商品id
     */
    private List<Integer> goodsIds;
    /**
     * 规格id
     */
    private List<Integer> productId;
}
