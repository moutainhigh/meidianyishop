package com.meidianyi.shop.service.pojo.shop.goods.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *  对外接口-商品详情-入参
 * @author 李晓冰
 * @date 2020年05月28日
 */
@Data
public class ApiGoodsDetailParam {
    @JsonProperty("goods_id")
    private Integer goodsId;
}
