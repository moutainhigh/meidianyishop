package com.meidianyi.shop.service.pojo.shop.store.goods;

import lombok.Data;

import java.util.List;

/**
 * 校验药房商品数量是否足够请求参数
 * @author 李晓冰
 * @date 2020年09月03日
 */
@Data
public class StoreGoodsCheckQueryParam {
    private List<StoreGoodsBaseCheckInfo> goodsItems;
}
