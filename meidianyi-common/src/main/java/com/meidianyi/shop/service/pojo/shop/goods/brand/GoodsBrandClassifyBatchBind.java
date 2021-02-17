package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Data;

import java.util.List;

/**
 * 品牌批量绑定至品牌分类
 * @author 李晓冰
 * @date 2019年07月26日
 */
@Data
public class GoodsBrandClassifyBatchBind {
    /** 品牌分类id */
    private Integer classifyId;
    /** 待绑定的品牌id集合 */
    private List<Integer> brandIds;
}
