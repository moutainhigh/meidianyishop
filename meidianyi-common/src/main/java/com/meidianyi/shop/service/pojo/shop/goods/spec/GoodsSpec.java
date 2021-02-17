package com.meidianyi.shop.service.pojo.shop.goods.spec;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 货品规格名
 * @author 李晓冰
 * @date 2019年07月05日
 */
@Data
@NoArgsConstructor
public class GoodsSpec {
    private Integer specId;
    private String specName;
    private Integer goodsId;

    private List<GoodsSpecVal> goodsSpecVals;

    public GoodsSpec(String specName, List<GoodsSpecVal> goodsSpecVals) {
        this.specName = specName;
        this.goodsSpecVals = goodsSpecVals;
    }
}
