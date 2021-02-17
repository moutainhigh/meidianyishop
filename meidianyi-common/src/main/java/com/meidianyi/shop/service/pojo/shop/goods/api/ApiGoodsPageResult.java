package com.meidianyi.shop.service.pojo.shop.goods.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.util.api.ApiPageResult;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 商品导出列表分页类-此类是由于php目前规定的对接文档字段不统一导致，
 * 后期可以直接在ApiPageResult类进行统一
 * @author 李晓冰
 * @date 2020年05月28日
 */
@Getter
@Setter
public class ApiGoodsPageResult extends ApiPageResult {
    @JsonProperty("total_goods_count")
    private Integer totalGoodsCount;
    @JsonProperty("goods_list")
    private List<ApiGoodsListVo> goodsList;
}
