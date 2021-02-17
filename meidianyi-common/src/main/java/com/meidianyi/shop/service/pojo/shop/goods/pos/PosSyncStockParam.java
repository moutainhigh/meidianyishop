package com.meidianyi.shop.service.pojo.shop.goods.pos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * pos 对接 同步商品数据接口参数
 * @author 李晓冰
 * @date 2020年04月28日
 */
@Data
public class PosSyncStockParam {
    /**pos门店id(不是小程序的店铺id)*/
    @JsonProperty("shop_id")
    private Integer posShopId;

    @JsonProperty("prd_sn")
    private String prdSn;

    private Float number;
}
