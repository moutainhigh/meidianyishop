package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

import java.util.List;

/**
 * 统计商品数量
 * @author 李晓冰
 * @date 2020年01月15日
 */
@Data
public class GoodsNumCountParam {
    private Integer sortId;
    private Integer labelId;
    private Integer brandId;
    /**商品id范围限制*/
    private List<Integer> goodsIds;
}
