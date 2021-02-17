package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * 分销员收藏/取消收藏入参
 * @author panjing
 * @data 2020/7/28 15:50
 */
@Data
public class DistributorCollectionParam {
    /**
     * 分销员id
     */
    private Integer distributorId;

    /**
     * 分销员收藏/取消收藏的商品id
     */
    private Integer goodsId;

    /**
     * 0: 收藏
     * 1: 取消收藏
     */
    private Byte type;
}
