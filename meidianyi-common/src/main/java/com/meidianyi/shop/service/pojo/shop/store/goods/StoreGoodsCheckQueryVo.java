package com.meidianyi.shop.service.pojo.shop.store.goods;

import lombok.Data;

import java.util.List;

/**
 * 商品数量足够的药房信息
 * @author 李晓冰
 * @date 2020年09月03日
 */
@Data
public class StoreGoodsCheckQueryVo {
    private List<String> shopList;
}
