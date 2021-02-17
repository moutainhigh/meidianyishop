package com.meidianyi.shop.service.pojo.shop.order.goods.store;

import lombok.Data;

import java.util.List;

/**
 * 查询药品数量足够药店列表集合
 * @author 李晓冰
 * @date 2020年09月09日
 */
@Data
public class OrderStockEnoughQueryParam {
    List<OrderStoreGoodsBase> goodsItems;
}
