package com.meidianyi.shop.service.pojo.shop.config.trade;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.util.Util;

import lombok.Data;

import java.util.List;

/**
 * @author liufei
 * @date 2019/7/11
 */
@Data
public class GoodsPackageParam {
    @JsonProperty(value = "add_goods")
    @JsonAlias({"add_goods", "addGoods"})
    public List<Integer> addGoods;
    /**
     * 平台分类
     */
    @JsonProperty(value = "add_cate")
    @JsonAlias({"add_cate", "addCate"})
    public List<Integer> addCate;
    /** 商家分类 */
    @JsonProperty(value = "add_sort")
    @JsonAlias({"add_sort", "addSort"})
    public List<Integer> addSort;
    @JsonProperty(value = "add_label")
    @JsonAlias({"add_label", "addLabel"})
    public List<Integer> addLabel;
    @JsonProperty(value = "add_brand")
    @JsonAlias({"add_brand", "addBrand"})
    public List<Integer> addBrand;

    @Override
    public String toString() {
        return Util.toJson(this);
    }
}
