package com.meidianyi.shop.service.pojo.shop.order.goods.store;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年08月27日
 */
@Data
public class StoreGoodsNumConfirmParam {
    private String shopSn;
    private List<OrderStoreGoodsBase> goodsItems;
}
