package com.meidianyi.shop.service.pojo.shop.order.goods.store;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 药房门店pos单推送类
 * @author 李晓冰
 * @date 2020年08月27日
 */
@Data
public class OrderStorePosBo {
    private String shopSn;
    private String orderSn;
    private String orderTime;
    private String userName;
    private String userPhone;
    private String orderAddress;
    private String orderMemo;
    private Byte isPickUp;
    private BigDecimal goodsSumPrice;
    private List<OrderStoreGoodsBo> goodsInfos;
}
