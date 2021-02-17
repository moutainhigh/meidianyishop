package com.meidianyi.shop.service.pojo.shop.goods.pos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * pos同步规格信息请求数据参数
 *
 * @author 李晓冰
 * @date 2020年04月28日
 */
@Data
public class PosSyncProductParam {
    /**pos门店id(不是小程序的店铺id)*/
    @JsonProperty("shop_id")
    Integer shopId;

    /**对应商品规格信息*/
    @JsonProperty("goods_list")
    List<PosSyncGoodsPrdParam> goodsList;
}
