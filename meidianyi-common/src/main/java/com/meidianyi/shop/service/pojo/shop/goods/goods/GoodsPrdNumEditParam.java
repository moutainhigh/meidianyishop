package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

import java.util.List;

/**
 * 多规格商品数量修改
 * @author 李晓冰
 * @date 2020年05月08日
 */
@Data
public class GoodsPrdNumEditParam {
    private Integer goodsId;
    private List<PrdPriceNumberParam> prdNumInfos;
}
