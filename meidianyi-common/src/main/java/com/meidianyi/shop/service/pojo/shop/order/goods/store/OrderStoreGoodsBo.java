package com.meidianyi.shop.service.pojo.shop.order.goods.store;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author 李晓冰
 * @date 2020年08月27日
 */
@Getter
@Setter
public class OrderStoreGoodsBo extends OrderStoreGoodsBase{
    private BigDecimal goodsPrice;
}
